package com.example.carparkingapi.config.map.struct;

import com.example.carparkingapi.domain.Admin;
import com.example.carparkingapi.dto.AdminDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    AdminDTO adminToAdminDTO(Admin admin);

}
