package com.mycompany.myapp.service;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseRequest;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.InferenceConfiguration;
import software.amazon.awssdk.services.bedrockruntime.model.Message;
import software.amazon.awssdk.services.bedrockruntime.model.SystemContentBlock;

/** Calls Amazon Bedrock Converse using the application's configured model. */
@Service
public class BedrockAiService {

    private final String modelId;
    private final String region;
    private final String profile;
    private final String accessKeyId;
    private final String secretAccessKey;
    private final String sessionToken;
    private final int maxTokens;
    private final float temperature;

    public BedrockAiService(
        @Value("${AI_MODEL_ID:amazon.nova-lite-v1:0}") String modelId,
        @Value("${AWS_REGION:us-east-1}") String region,
        @Value("${AWS_PROFILE:}") String profile,
        @Value("${AWS_ACCESS_KEY_ID:}") String accessKeyId,
        @Value("${AWS_SECRET_ACCESS_KEY:}") String secretAccessKey,
        @Value("${AWS_SESSION_TOKEN:}") String sessionToken,
        @Value("${AI_MAX_TOKENS:500}") int maxTokens,
        @Value("${AI_TEMPERATURE:0.3}") float temperature
    ) {
        this.modelId = modelId;
        this.region = region;
        this.profile = profile;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.sessionToken = sessionToken;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
    }

    public String answer(String systemPrompt, String userPrompt) {
        AwsCredentialsProvider credentialsProvider;
        if (!accessKeyId.isBlank() && !secretAccessKey.isBlank()) {
            if (!sessionToken.isBlank()) {
                credentialsProvider = StaticCredentialsProvider.create(
                    AwsSessionCredentials.create(accessKeyId, secretAccessKey, sessionToken)
                );
            } else {
                AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
                credentialsProvider = StaticCredentialsProvider.create(credentials);
            }
        } else if (!profile.isBlank()) {
            credentialsProvider = ProfileCredentialsProvider.create(profile);
        } else {
            credentialsProvider = DefaultCredentialsProvider.create();
        }
        try (
            BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build()
        ) {
            ConverseRequest request = ConverseRequest.builder()
                .modelId(modelId)
                .system(SystemContentBlock.builder().text(systemPrompt).build())
                .messages(Message.builder().role(ConversationRole.USER).content(ContentBlock.builder().text(userPrompt).build()).build())
                .inferenceConfig(InferenceConfiguration.builder().maxTokens(maxTokens).temperature(temperature).build())
                .build();

            ConverseResponse response = client.converse(request);
            return response.output().message().content().stream().map(ContentBlock::text).filter(Objects::nonNull).findFirst().orElseThrow(
                () -> new IllegalStateException("Amazon Bedrock returned an empty response")
            );
        }
    }
}
