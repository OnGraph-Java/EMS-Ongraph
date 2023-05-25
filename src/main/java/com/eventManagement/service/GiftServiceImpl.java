package com.eventManagement.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.eventManagement.dto.GiftDto;
import com.eventManagement.model.Gift;
import com.eventManagement.repository.GiftRepository;

@Service
public class GiftServiceImpl implements GiftService {

	private Logger logger = LoggerFactory.getLogger(GiftServiceImpl.class.getName());

	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	public static String projectlocalPath = System.getProperty("user.dir");

	@Autowired
	GiftRepository giftRepository;

	@Override
	public String addGift(GiftDto giftDto, MultipartFile file) {
		logger.info("Add gift service started :");
		String response = "";
		Gift gift = new Gift();
		try {
			gift = parseGiftDto(gift, giftDto);

			String imageName = saveFileInSystem(file);
			gift.setImageName(imageName);
			gift.setCreatedOn(sdf.parse(sdf.format(new Date())));
			giftRepository.save(gift);
			logger.info("Add gift service ended :");
			response = "Successfully saved Gift";
		} catch (Exception ex) {
			logger.error("Exception got while saving Gift : " + ex.getMessage());
			response = "Exception got while saving Gift : " + ex.getMessage();
		}
		return response;
	}

	@Override
	public String updateGift(Long giftId, @Valid GiftDto giftDto, MultipartFile file) {

		try {
			logger.info("Update gift service started :");
			Optional<Gift> gift = giftRepository.findById(giftId);
			if (gift.isEmpty()) {
				return "No such gift exists";
			}
			Gift updatedGift = parseGiftDto(gift.get(), giftDto);
			String imageName = saveFileInSystem(file);
			updatedGift.setLastUpdated(sdf.parse(sdf.format(new Date())));
			updatedGift.setImageName(imageName);
			giftRepository.save(updatedGift);
		} catch (Exception ex) {
			logger.error("Exception got while updating gift :" + ex.getMessage());
			return "Exception got while updating gift :" + ex.getMessage();
		}
	    logger.info("Update gift service ended.");
		return "Successfully Saved Gift";
	}

	public Gift parseGiftDto(Gift gift, GiftDto giftDto) {
	    logger.info("Parsing GiftDto to Gift object");
		gift.setGiftTitle(giftDto.getGiftTitle());
		gift.setRedeemRequirePoints(giftDto.getRedeemRequirePoints());
		gift.setAdminId(giftDto.getAdminId());
		gift.setAvailableFor(giftDto.getAvailableFor());
		gift.setGiftDetail(giftDto.getGiftDetail());
	    logger.info("GiftDto parsed successfully");
		return gift;
	}

	public String saveFileInSystem(MultipartFile file) throws Exception {
		logger.info("Savefile in system service started :");
		String UPLOAD_DIR = "event\\images\\";
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			Path path = Paths.get(UPLOAD_DIR + fileName);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			logger.info("Savefile in system service ended :");
		} catch (IOException e) {
			throw new Exception("Exception got while saving files : " + e.getMessage());
		} catch (UncheckedIOException e) {
			throw new Exception("An UncheckedIOException occurred: " + e.getMessage());
		}
		return UPLOAD_DIR + fileName;

	}

	@Override
	public Gift findGift(Long giftId) {
		logger.info("Find gift service started :");
		Gift gift = null;
		try {
			gift = giftRepository.findById(giftId).get();
			String str = gift.getImageName();
			str = projectlocalPath + "\\" + str;
			gift.setImageName(str);
			logger.info("Find gift service ended :");

		} catch (Exception ex) {
			logger.error("Exception got while fetching gift : " + ex.getMessage());
		}
		return gift;
	}

	@Override
	public List<Gift> findAllGift(Long adminId, String sortBy, String title) {
		logger.info("Find all gift service started :");
		List<Gift> giftList;

		giftList = giftRepository.findAllGiftByAdminId(adminId, sortBy, title.toLowerCase());

		for (Gift gift : giftList) {
			String str = gift.getImageName();
			str = projectlocalPath + "\\" + str;
			gift.setImageName(str);
		}
		logger.info("Find all gift service ended :");

		return giftList;
	}

}
