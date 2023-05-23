package com.eventManagement.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class GiftDto {

	@NotBlank(message = "giftTitle is null")
    @Size(min = 2, max = 20)
	private String giftTitle;

	@NotBlank(message = "giftDetail is null")
	private String giftDetail;

	@NotNull(message = "availableFor is null")
	private Integer availableFor;

	@NotNull(message = "redeemRequirePoints is null")
	private Long redeemRequirePoints;
	
	@NotNull(message = "adminId is null")
	private Long adminId;

	public String getGiftTitle() {
		return giftTitle;
	}

	public void setGiftTitle(String giftTitle) {
		this.giftTitle = giftTitle;
	}

	public String getGiftDetail() {
		return giftDetail;
	}

	public void setGiftDetail(String giftDetail) {
		this.giftDetail = giftDetail;
	}

	public Integer getAvailableFor() {
		return availableFor;
	}

	public void setAvailableFor(Integer availableFor) {
		this.availableFor = availableFor;
	}

	public Long getRedeemRequirePoints() {
		return redeemRequirePoints;
	}

	public void setRedeemRequirePoints(Long redeemRequirePoints) {
		this.redeemRequirePoints = redeemRequirePoints;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}
	
	
}
