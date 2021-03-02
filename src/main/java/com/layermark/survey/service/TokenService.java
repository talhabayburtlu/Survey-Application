package com.layermark.survey.service;

import com.layermark.survey.dao.TokenRepository;
import com.layermark.survey.entity.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token save(Token token) {
        tokenRepository.save(token);
        return token;
    }

    public void deleteById(int id) {
        tokenRepository.deleteById(id);
    }
}
