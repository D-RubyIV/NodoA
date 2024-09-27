package org.example.demo.dto.category.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo.dto.group.GroupCreate;
import org.example.demo.dto.group.GroupUpdate;
import org.example.demo.enums.EStatus;
import org.example.demo.validate.ImageFileConstraint;
import org.example.demo.validate.ImageSizeConstraint;
import org.example.demo.validate.NoSpecialCharacterConstraint;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryRequestDTO {
    @Length(message = "Length-5-25", min = 5, max = 25, groups = {GroupCreate.class, GroupUpdate.class})
    @NotNull(message = "NotNull", groups = {GroupCreate.class, GroupUpdate.class})
    @NotBlank(message = "NotBlank", groups = {GroupCreate.class, GroupUpdate.class})
    @NoSpecialCharacterConstraint(message = "NoSpecialCharacter", groups = {GroupCreate.class, GroupUpdate.class})
    private String name;

//    @Length(message = "Length-5-50", min = 5, max = 50, groups = {GroupCreate.class, GroupUpdate.class})
//    @NotNull(message = "NotNull", groups = {GroupCreate.class, GroupUpdate.class})
//    @NotBlank(message = "NotBlank", groups = {GroupCreate.class, GroupUpdate.class})
    private String description;

    @Length(message = "Length-5-15", min = 5, max = 15, groups = {GroupCreate.class})
    @NotNull(message = "NotNull", groups = {GroupCreate.class})
    @NotBlank(message = "NotBlank", groups = {GroupCreate.class})
    @NoSpecialCharacterConstraint(message = "NoSpecialCharacter", groups = {GroupCreate.class})
    private String categoryCode;

    @NotNull(message = "NotNull", groups = {GroupUpdate.class})
    @Enumerated(EnumType.STRING)
    private EStatus status;

    @ImageFileConstraint(message = "ImageValidate", groups = {GroupCreate.class, GroupUpdate.class})
    @ImageSizeConstraint(maxSize = 2097152, message = "Max2MB", groups = {GroupCreate.class, GroupUpdate.class}) // Kích thước tối đa là 2MB
    private MultipartFile file;
}
