package com.accenture.labs.nanoleaf.service;

import java.util.Arrays;
import java.util.List;

import com.accenture.labs.nanoleaf.dto.EventDto;
import com.accenture.labs.nanoleaf.dto.LayoutDto;
import com.accenture.labs.nanoleaf.dto.RGBDto;

public interface NanoleafService {
	
	public static final String COMMAND_DISPLAY = "display";
	public static final String TYPE_STATIC = "static";
	public static final RGBDto BLANK_COLOR = RGBDto.builder().red(0).green(0).blue(0).build();
	public static final RGBDto ACC_STRA = RGBDto.builder().red(235).blue(37).green(52).build();
	public static final RGBDto ACC_CONS = RGBDto.builder().red(113).blue(245).green(44).build();
	public static final RGBDto ACC_DIGI = RGBDto.builder().red(249).blue(84).green(212).build();
	public static final RGBDto ACC_TECH = RGBDto.builder().red(98).blue(63).green(210).build();
	public static final RGBDto ACC_OPER = RGBDto.builder().red(80).blue(249).green(185).build();
	public static final List<RGBDto> ALL_ACC = Arrays.asList(ACC_STRA, ACC_CONS, ACC_DIGI, ACC_TECH, ACC_OPER);

	public void randomPanel(List<RGBDto> dto);
	public void randomSoftPanel(List<RGBDto> dtoList, int iterations);
	public Boolean switchOn();
	public Boolean switchOff();
	public LayoutDto getLayout();
	public void setAnimdata(RGBDto rgb, int panelId, String command, String type, Boolean loop);
	public void stopCurrentEffect(Boolean clear);
	public void executeTransition(List<RGBDto> list, int panelId);
	public Boolean isRhythmConnected();
	public Boolean isRhythmActive();
	public void setRhythmModeInternal();
	public void blink();
	public void getAudio();
	
	public void initGame();
	public void addEvent(EventDto event);
	
}
