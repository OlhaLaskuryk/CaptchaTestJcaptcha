package org.owasp.captcha;

import java.security.SecureRandom;
import java.util.Random;

import nl.captcha.text.producer.TextProducer;


public class IntegerTextProducer implements TextProducer {

    private static final Random RAND = new SecureRandom();
    private static final int DEFAULT_LENGTH = 5;
    
    private final int _length;

    public IntegerTextProducer() {
    	this(DEFAULT_LENGTH);
    }
    
    public IntegerTextProducer(int length) {
    	_length = length;
    }
    
    @Override
    public String getText() {
        String capText = "";
        for (int i = 0; i < _length; i++) {
            capText += RAND.nextInt(10);
        }

        return capText;
    }
}