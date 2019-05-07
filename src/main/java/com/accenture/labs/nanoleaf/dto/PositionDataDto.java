package com.accenture.labs.nanoleaf.dto;

import com.accenture.labs.nanoleaf.dto.BooleanValueDto.BooleanValueDtoBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor	
public class PositionDataDto {
	int panelId;
	int x;
	int y;
	int o;
	int shapeType;
}
