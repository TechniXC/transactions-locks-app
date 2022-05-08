package ru.jpoint.transactionslocksapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Likes {

    @JsonProperty("speakerId")
    private Long speakerId;

    @JsonProperty("talkName")
    private String talkName;

    @JsonProperty("likes")
    private int likes;
}
