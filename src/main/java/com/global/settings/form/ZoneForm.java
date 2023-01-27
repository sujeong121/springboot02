package com.global.settings.form;

import com.global.domain.Zone;
import lombok.Data;

@Data
public class ZoneForm {

  private String zoneName;

  public String getCityName(){
    return zoneName.substring(0, zoneName.indexOf("("));
  }

  public String getProvinceName(){
    return zoneName.substring(zoneName.indexOf("/") + 1);
  }

  public String getLocalNameCity(){
    return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
  }

  public Zone getZone(){
    return Zone.builder().city(this.getCityName())
               .localNameOfCity(this.getLocalNameCity())
               .province(this.getProvinceName()).build();
  }

}
