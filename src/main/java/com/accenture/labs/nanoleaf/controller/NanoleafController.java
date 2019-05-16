package com.accenture.labs.nanoleaf.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.labs.nanoleaf.dto.EventDto;
import com.accenture.labs.nanoleaf.dto.RGBDto;
import com.accenture.labs.nanoleaf.service.NanoleafService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class NanoleafController {
	
	@Autowired
	NanoleafService service;
	
	@PostMapping("/blink")
	public void blink() {
		log.debug("blink method");
		service.stopCurrentEffect(Boolean.TRUE);
		
		service.switchOn();
				
		service.blink();
	}

	@PostMapping("/test")
	public void method() {
		log.debug("Test method");
		service.stopCurrentEffect(Boolean.TRUE);
		List<RGBDto> colors = Arrays.asList(
				NanoleafService.ACC_STRA, NanoleafService.ACC_CONS, NanoleafService.ACC_DIGI, NanoleafService.ACC_TECH, NanoleafService.ACC_OPER
				);
		
		service.switchOn();
				
		service.randomSoftPanel(colors, 10);
	}
	
	@PostMapping("/technology")
	public void technology() {
		log.debug("Test method");
		service.stopCurrentEffect(Boolean.FALSE);
		List<RGBDto> colors = Arrays.asList(
					NanoleafService.ACC_TECH
				);
		
		service.switchOn();
				
		service.randomSoftPanel(colors, 10);
	}
	
	@PostMapping("/digital")
	public void digital() {
		log.debug("Test method");
		service.stopCurrentEffect(Boolean.FALSE);
		List<RGBDto> colors = Arrays.asList(
					NanoleafService.ACC_DIGI
				);
		
		service.switchOn();
				
		service.randomSoftPanel(colors, 10);
	}
	
	@PostMapping("/strategy")
	public void strategy() {
		log.debug("Test method");
		service.stopCurrentEffect(Boolean.FALSE);
		List<RGBDto> colors = Arrays.asList(
					NanoleafService.ACC_STRA
				);
		
		service.switchOn();
				
		service.randomSoftPanel(colors, 10);
	}
	
	@PostMapping("/consulting")
	public void consulting() {
		log.debug("Test method");
		service.stopCurrentEffect(Boolean.FALSE);
		List<RGBDto> colors = Arrays.asList(
					NanoleafService.ACC_CONS
				);
		
		service.switchOn();
				
		service.randomSoftPanel(colors, 10);
	}
	
	@PostMapping("/operations")
	public void operations() {
		log.debug("Test method");
		service.stopCurrentEffect(Boolean.FALSE);
		List<RGBDto> colors = Arrays.asList(
					NanoleafService.ACC_OPER
				);
		
		service.switchOn();
				
		service.randomSoftPanel(colors, 10);
	}
	
	
	@PostMapping("/testR")
	public void methodR() {
		log.debug("TestR method");
		service.stopCurrentEffect(Boolean.TRUE);
		service.setRhythmModeInternal();
		if (service.isRhythmConnected() && service.isRhythmActive()) {
			log.debug("Active");
		}

		service.switchOff();
	}
	
	@PostMapping("/switchOff")
	public void switchOff() {
		service.switchOff();
	}
	
	
	@PostMapping("/stopEffect")
	public void stopEffect() {
		service.stopCurrentEffect(Boolean.TRUE);
	}
	
	@PostMapping("/audio")
	public void audio() {
		service.getAudio();
	}
	
	@PostMapping("initGame")
	public void initGame() {
		service.initGame();
	}
	
	@PostMapping("addEvent")
	public void addEvent(@RequestBody EventDto event) {
		service.addEvent(event);
	}
}
