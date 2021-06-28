package com.salam.util;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Component
@AllArgsConstructor
@ConfigurationProperties(prefix = "stockprops")
@Getter
@Setter
public class InputProperties {
	
	private List<String> stocknames;
	
	private List<String> stocksourceapi;

}
