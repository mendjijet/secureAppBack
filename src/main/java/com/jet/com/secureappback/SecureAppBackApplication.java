package com.jet.com.secureappback;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication//(exclude = {SecurityAutoConfiguration.class})
@EnableEncryptableProperties
public class SecureAppBackApplication {
  private static final int STRENGHT = 12;

  public static void main(String[] args) {
    SpringApplication.run(SecureAppBackApplication.class, args);
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(STRENGHT);
  }
}
