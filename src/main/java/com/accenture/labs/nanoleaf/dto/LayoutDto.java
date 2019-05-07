package com.accenture.labs.nanoleaf.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor	
public class LayoutDto {
	int numPanels;
	int sideLength;
	List<PositionDataDto> positionData;
}
