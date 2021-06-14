package com.salam.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "STOCK_MONTHLY_SUMM")
public class MonthlyData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6946508724652640194L;

	@Override
	public String toString() {
		return "MonthlyData [monthEndDate=" + smsMonthEndDate + ", smsOpen=" + smsOpen + ", smsHigh=" + smsHigh
				+ ", smsLow=" + smsLow + ", smsClose=" + smsClose + ", smsVolume=" + smsVolume + "]";
	}

	@Id
	@Column(name = "SMS_STOCK_NAME", nullable = false)
	private String smsStockName;
	
	@Id
	@Column(name = "SMS_MONTH_END_DATE", nullable = false)
	private LocalDate smsMonthEndDate;

	@Column(name = "SMS_OPEN", nullable = false)
	private BigDecimal smsOpen;

	@Column(name = "SMS_HIGH", nullable = false)
		private BigDecimal smsHigh;

	@Column(name = "SMS_LOW", nullable = false)
	private BigDecimal smsLow;

	@Column(name = "SMS_CLOSE", nullable = false)
	private BigDecimal smsClose;

	@Column(name = "SMS_VOLUME")
	private Long smsVolume;

}
