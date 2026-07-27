package com.kapil.jobtracker.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRequest {

        @NotBlank(message = "Company name cannot be blank")
        @Size(max = 150, message = "Company name cannot exceed by 150 characters")
        private String name;

        @Size(max = 255, message = "Website exceed by 255 characters")
        @URL(message = "Enter valid URL")
        private String website;

        @Size(max = 150, message = "Location cannot exceed by 150 characters")
        private String location;

        @Size(max = 100, message = "Industry name cannot exceed by 100 characters")
        private String industry;

        @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
        private String notes;
}
