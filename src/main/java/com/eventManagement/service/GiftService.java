package com.eventManagement.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.multipart.MultipartFile;

import com.eventManagement.dto.GiftDto;
import com.eventManagement.model.Gift;

public interface GiftService {

	String addGift(GiftDto giftDto, MultipartFile file);

	String updateGift(Long giftId, @Valid GiftDto giftDto, MultipartFile file);

	Gift findGift(Long giftId);

	List<Gift> findAllGift(Long adminId, String sortBy, String title);

}
