package com.eventManagement.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.eventManagement.dto.GiftDto;
import com.eventManagement.model.Gift;
import com.eventManagement.repository.GiftRepository;

@Service
public class GiftServiceImpl implements GiftService {

	private Logger logger = LoggerFactory.getLogger(GiftServiceImpl.class.getName());

	DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static String projectlocalPath = System.getProperty("user.dir");

	@Autowired
	GiftRepository giftRepository;

	@Override
	public String addGift(GiftDto giftDto, MultipartFile file) {
		logger.info("Add gift service started :");
		LocalDate currentDate = LocalDate.now();

		String response = "";
		Gift gift = new Gift();
		try {
			gift = parseGiftDto(gift, giftDto);

			String imageName = saveFileInSystem(file);
			gift.setImageName(imageName);
			gift.setCreatedOn(LocalDate.parse(currentDate.format(df), df));
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
		LocalDate currentDate = LocalDate.now();

		try {
			logger.info("Update gift service started :");
			Optional<Gift> gift = giftRepository.findById(giftId);
			if (gift.isEmpty()) {
				return "No such gift exists";
			}
			Gift updatedGift = parseGiftDto(gift.get(), giftDto);
			if (file != null) {
				String imageName = saveFileInSystem(file);
				updatedGift.setImageName(imageName);
			}
			updatedGift.setLastUpdated(LocalDate.parse(currentDate.format(df), df));
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
		String fileName = file.getOriginalFilename();
		try {
			Path imagePath = Path.of("src", "main", "resources", "static", "img", fileName);
			Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

			logger.info("Savefile in system service ended :");
		} catch (IOException e) {
			throw new Exception("Exception got while saving files : " + e.getMessage());
		} catch (UncheckedIOException e) {
			throw new Exception("An UncheckedIOException occurred: " + e.getMessage());
		}
		return fileName;

	}

	@Override
	public Gift findGift(Long giftId) {
		logger.info("Find gift service started :");
		Gift gift = null;
		try {
			gift = giftRepository.findById(giftId).get();
			String str = gift.getImageName();
			str = str.replace("event//images//", "");
			gift.setImageName(str);
			logger.info("Find gift service ended :");

		} catch (Exception ex) {
			logger.error("Exception got while fetching gift : " + ex.getMessage());
		}
		return gift;
	}

	@Override
	public List<Gift> findAllGift(Long adminId, String sortBy, String title, boolean isDashboard, String fromDate,
			String endDate) {
		logger.info("Find all gift service started :");
		List<Gift> giftList;
		if (sortBy.equals("points")) {
			sortBy = "redeemRequirePoints";
		}
		if (isDashboard) {
			 Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
			PageRequest pageReq = PageRequest.of(0, 5, sort);
			if (fromDate != null && endDate != null) {
				LocalDate localStartDate = LocalDate.parse(fromDate, df);
				LocalDate localEndDate = LocalDate.parse(endDate, df);
				giftList = giftRepository.findAllGiftByAdminIdPageWithDates(adminId, sortBy, title.toLowerCase(),
						localStartDate, localEndDate, pageReq);
			} else if (fromDate != null) {
				LocalDate localStartDate = LocalDate.parse(fromDate, df);
				giftList = giftRepository.findAllGiftByAdminIdPageWithStartDate(adminId, sortBy, title.toLowerCase(),
						localStartDate, pageReq);
			} else if (endDate != null) {
				LocalDate localEndDate = LocalDate.parse(endDate, df);
				giftList = giftRepository.findAllGiftByAdminIdPageWithEndDate(adminId, sortBy, title.toLowerCase(),
						localEndDate, pageReq);
			} else {
				giftList = giftRepository.findAllGiftByAdminIdPage(adminId, sortBy, title.toLowerCase(), pageReq);
			}
		} else {
			 Sort sort = Sort.by(Sort.Direction.DESC, sortBy);

			giftList = giftRepository.findAllGiftByAdminId(adminId, title.toLowerCase(), sort);
		}
		for (Gift gift : giftList) {
			String str = gift.getImageName();
			str = str.replace("event//images//", "");
			gift.setImageName(str);
		}
		logger.info("Find all gift service ended :");

		return giftList;
	}

}
