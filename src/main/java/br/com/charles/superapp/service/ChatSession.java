package br.com.charles.superapp.infra.security;

public class ChatSession {
    public String sessionId;
    public String currentStep;
    public Map<String, String> data = new HashMap<>();
}
