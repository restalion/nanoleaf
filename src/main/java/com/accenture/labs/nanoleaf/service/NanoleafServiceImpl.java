package com.accenture.labs.nanoleaf.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.accenture.labs.nanoleaf.dto.BooleanValueDto;
import com.accenture.labs.nanoleaf.dto.EffectDto;
import com.accenture.labs.nanoleaf.dto.EventDto;
import com.accenture.labs.nanoleaf.dto.LayoutDto;
import com.accenture.labs.nanoleaf.dto.PositionDataDto;
import com.accenture.labs.nanoleaf.dto.RGBDto;
import com.accenture.labs.nanoleaf.dto.RhythmModeDto;
import com.accenture.labs.nanoleaf.dto.StateDto;
import com.accenture.labs.nanoleaf.dto.WriteDto;

import ch.qos.logback.core.net.SyslogOutputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NanoleafServiceImpl implements NanoleafService {

	Map<Integer, RGBDto> currentStatus = new HashMap<>();
	int gameStep;
	
	@Value("${authToken}")
	String authToken;
	
	@Value("${baseUrl}")
	String baseUrl;
	
	@Value("${nanoleaf.ip}")
	String ip;
	
	@Value("${nanoleaf.port}")
	String port;
	
	@Value("${put.switch-on-off}") 
	String switchOnOff;
	
	@Value("${put.identify}")
	String identify;
	
	@Value("${get.panel-layout}")
	String panelLayout;
	
	@Value("${put.update-effect}")
	String updateEffect;
	
	@Value("${get.rhythmConnected}")
	String rhythConnected;
	
	@Value("${get.rhythmActive}")
	String rhythActive;
	
	@Value("${put.rhythmMode}")
	String rhythmMode;
	
	Boolean effectOn = Boolean.FALSE;
	
	RestTemplate template = new RestTemplate();	
	
	public void setAnimdata(RGBDto rgb, int panelId, String command, String type, Boolean loop) {
		WriteDto writeDto = WriteDto.builder().write(EffectDto.builder().command(command).animType(type).loop(loop).animData(buildAnimdata(panelId, rgb)).build()).build();
		template.put(composeBaseUrl() + updateEffect, writeDto);
	}
	
	private String buildAnimdata(int panelId, RGBDto rgb) {
		return "1 " + panelId + " 0 " + rgb.getRed() + " " + rgb.getGreen() + " " + rgb.getBlue() + " 1";
	}

	@Async
	public void randomPanel(List<RGBDto> dtoList) {
		effectOn = Boolean.TRUE;
		Random ram = new Random();
		LayoutDto layout = getLayout();
		while (effectOn) {
			int panelId = layout.getPositionData().get(ram.nextInt(layout.getPositionData().size())).getPanelId();
			RGBDto color = dtoList.get(ram.nextInt(dtoList.size()));
			setAnimdata(color, panelId, COMMAND_DISPLAY, TYPE_STATIC, Boolean.FALSE);
			currentStatus.put(panelId, color);
			mywait(500l);
		}
	}
	
	@Async
	public void randomSoftPanel(List<RGBDto> dtoList, int iterations) {
		effectOn = Boolean.TRUE;
		Random ram = new Random();
		LayoutDto layout = getLayout();
		while (effectOn) {
			int panelId = layout.getPositionData().get(ram.nextInt(layout.getPositionData().size())).getPanelId();
			RGBDto color = dtoList.get(ram.nextInt(dtoList.size()));
			RGBDto currentColor = getCurrentColor(panelId);
			List<RGBDto> list = getTransition(currentColor, color, iterations);
			executeTransition(list, panelId);
			setAnimdata(color, panelId, COMMAND_DISPLAY, TYPE_STATIC, Boolean.FALSE);
			currentStatus.put(panelId, color);
			mywait(500l);
		}
	}
	
	public void executeTransition(List<RGBDto> list, int panelId) {
		list.forEach(c -> {
			setAnimdata(c, panelId, COMMAND_DISPLAY, TYPE_STATIC, Boolean.FALSE);
			log.debug("Panel " + panelId + " | color: " + c);
			mywait(200l);
		});
	}
	
	public RGBDto getCurrentColor(int panelId) {
		if (currentStatus.get(panelId) != null) {
			return currentStatus.get(panelId);
		} else {
			return BLANK_COLOR;
		}
	}
	
	public List<RGBDto> getTransition(RGBDto currentColor, RGBDto newColor, int numTransitions) {
		List<RGBDto> transitions = new ArrayList<>();
		int red = currentColor.getRed();
		int green = currentColor.getGreen();
		int blue = currentColor.getBlue();
		
		int redDiff = (newColor.getRed() - red) / numTransitions;
		int greenDiff = (newColor.getGreen() - green) / numTransitions;
		int blueDiff = (newColor.getBlue() - blue) / numTransitions;
		
		int tred = red;
		int tgreen = green;
		int tblue = blue;
		
		for (int i = 0; i < numTransitions; i++) {
			tred = tred + redDiff;
			tgreen = tgreen + greenDiff;
			tblue = tblue + blueDiff;
			RGBDto nextColor = RGBDto.builder().red(tred).green(tgreen).blue(tblue).build();
			transitions.add(nextColor);
		}
		return transitions;
	}
	
	public void stopCurrentEffect(Boolean clear) {
		effectOn = Boolean.FALSE;
		mywait(1000l);
		currentStatus = new HashMap<>();
		if (clear)
			getLayout().getPositionData().forEach(p -> setAnimdata(BLANK_COLOR, p.getPanelId(), COMMAND_DISPLAY, TYPE_STATIC, Boolean.FALSE));
	}
	
	public LayoutDto getLayout() {
		LayoutDto layout = template.getForObject(composeBaseUrl() + panelLayout, LayoutDto.class);
		return layout;
	}
	
	public Boolean switchOn() {
		return changeStatus(Boolean.TRUE);
	}
	
	public Boolean switchOff() {
		return changeStatus(Boolean.FALSE);
	}
	
	private Boolean changeStatus(Boolean status) {
		template.put(composeBaseUrl() + switchOnOff, StateDto.builder().on(BooleanValueDto.builder().value(status).build()).build());
		return status;
	}
	
	private String composeBaseUrl() {
		return "http://" + ip + ":" + port + baseUrl + "/" + authToken;
	}
	
	private void mywait(Long milis) {
		try
		{
		    Thread.sleep(milis);
		}
		catch(InterruptedException ex)
		{
		    Thread.currentThread().interrupt();
		}
	}
	
	public Boolean isRhythmConnected() {
		Boolean status = template.getForObject(composeBaseUrl() + rhythConnected, Boolean.class);
		return status;
	}
	
	public Boolean isRhythmActive() {
		Boolean status = template.getForObject(composeBaseUrl() + rhythActive, Boolean.class);
		return status;
	}
	
	public void setRhythmModeInternal() {
		template.put(composeBaseUrl() + rhythmMode, RhythmModeDto.builder().rhythmMode(0).build());
	}
	
	public void blink() {
		template.put(composeBaseUrl() + identify, null);
	}
	
	public void getAudio() {
		try {

			stopCurrentEffect(Boolean.TRUE);
			
			//AudioFormat format = new AudioFormat(22000,16,2,true,true);
			AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
			TargetDataLine line = AudioSystem.getTargetDataLine(format);
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); // format is an AudioFormat object
			log.debug("info " + info);
			if (!AudioSystem.isLineSupported(info)) {
				log.debug("Info: " + info);
			}
			// Obtain and open the line.
			try {
//				line = (TargetDataLine) AudioSystem.getLine(info);
				line.open(format);
			} catch (LineUnavailableException ex) {
				ex.printStackTrace(); 
			}
			
			// Assume that the TargetDataLine, line, has already
			// been obtained and opened.
			ByteArrayOutputStream out  = new ByteArrayOutputStream();
			int numBytesRead;
			byte[] data = new byte[line.getBufferSize() / 5];

			// Begin audio capture.
			line.start();
			
			effectOn = Boolean.TRUE;
			// Here, stopped is a global boolean set by another thread.
			while (effectOn) {
			   // Read the next chunk of data from the TargetDataLine.
			   numBytesRead =  line.read(data, 0, data.length);
			   // Save this chunk of data.
			   log.debug("Read from mic: " + numBytesRead);
			   out.write(data, 0, numBytesRead);
			   
			   short max;
			   if (numBytesRead >=0 ) {
			    max = (short) (data[0] + (data[1] << 8));
			    for (int p=2;p<numBytesRead-1;p+=2) {
			      short thisValue = (short) (data[p] + (data[p+1] << 8));
			      if (thisValue>max) max=thisValue;
			    }
			    log.debug("Max value is "+max);
			    if (max > 15000) {
			    	setAnimdata(ACC_TECH, nextPanel(), COMMAND_DISPLAY, TYPE_STATIC, Boolean.FALSE);
			    } else {
			    	setAnimdata(BLANK_COLOR, lastPanel(), COMMAND_DISPLAY, TYPE_STATIC, Boolean.FALSE);
			    }
			   }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<Integer> panels = Arrays.asList(211, 103, 9, 164, 49, 183, 63, 248, 206, 86, 25, 129, 117, 55, 47);
	private int indice = 0;
	private List<RGBDto> colors = Arrays.asList(RGBDto.builder().red(24).green(52).blue(15).build(),
			RGBDto.builder().red(48).green(104).blue(30).build(), RGBDto.builder().red(72).green(156).blue(45).build(),
			ACC_TECH);
	
	private int lastPanel() {
		if (indice > 0)
			return panels.get(indice--);
		else return panels.get(indice);
	}
	
	private int nextPanel() {
		if (indice < panels.size() -1) return panels.get(indice++);
		else return panels.get(indice);
	}
	
	public void initGame() {
		gameStep = 0;
		stopCurrentEffect(Boolean.TRUE);
	}
	
	public void addEvent(EventDto event) {
		// 3, 2, 0, 1S
		
		drawAllPanels(colors.get(event.getButtonId()));
		
	}

	public void drawAllPanels(RGBDto color) {
		panels.forEach(p -> setAnimdata(color, p, COMMAND_DISPLAY, TYPE_STATIC, Boolean.FALSE));
	}
	
}
