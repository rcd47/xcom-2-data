package com.github.rcd47.x2data.lib.unreal.mappings.base;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TDateTime {
	
	public float m_fTime;
	public int m_iDay;
	public int m_iMonth;
	public int m_iYear;
	
	public LocalDateTime toLocalDateTime() {
		return LocalDateTime.of(LocalDate.of(m_iYear, m_iMonth, m_iDay), LocalTime.ofSecondOfDay((int) m_fTime));
	}
	
}
