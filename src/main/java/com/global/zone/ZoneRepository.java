package com.global.zone;

import com.global.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

  Zone findByCityAndProvince(String cityName, String provinceName);
}
