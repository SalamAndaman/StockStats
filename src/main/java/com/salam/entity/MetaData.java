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
@Table(name = "STOCK_META_DATA")
public class MetaData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3754864309096024552L;

	@Id
	@Column(name = "SMD_STOCK_NAME", nullable = false)
	private String smdStockName;
	
	@Id
	@Column(name = "SMD_DATA_TYPE_DESCR", nullable = false)
	private String smdDataType;
	
	@Id
	@Column(name = "SMD_LAST_UPDATED_DATE", nullable = false)
	private LocalDate smdLastUpdated;

	@Override
	public String toString() {
		return "MetaData [smdStockName=" + smdStockName + ", smdDataType=" + smdDataType + ", smdLastUpdated="
				+ smdLastUpdated + "]";
	}
}
