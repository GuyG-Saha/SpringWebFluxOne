package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MovieInfo {
    @Id
    private String id;
    @NotBlank(message = "MovieInfo Name must be present")
    private String name;
    @NotNull
    @Positive(message = "Release year cannot be negative")
    private Integer year;
    private List<String> cast;
    private LocalDate releaseDate;
}
