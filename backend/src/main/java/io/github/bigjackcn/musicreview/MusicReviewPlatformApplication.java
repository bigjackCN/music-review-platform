package io.github.bigjackcn.musicreview;

import io.github.bigjackcn.musicreview.mapper.AlbumMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MusicReviewPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicReviewPlatformApplication.class, args);
    }

}