package com.example.carparkingapi.config.map.struct;

import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.dto.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO customerToCustomerDTO(Customer customer);
}
