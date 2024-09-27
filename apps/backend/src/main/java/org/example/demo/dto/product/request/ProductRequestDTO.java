package org.example.demo.dto.product.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo.dto.group.GroupCreate;
import org.example.demo.dto.group.GroupUpdate;
import org.example.demo.enums.EStatus;
import org.example.demo.validate.ImageFileConstraint;
import org.example.demo.validate.ImageSizeConstraint;
import org.example.demo.validate.NoSpecialCharacterConstraint;
import org.example.demo.validate.PositiveIntegerConstraint;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductRequestDTO {
    @Length(message = "Length-5-25", min = 5, max = 25, groups = {GroupCreate.class, GroupUpdate.class})  // 5-25 KI TU
    @NotNull(message = "NotNull", groups = {GroupCreate.class, GroupUpdate.class})
    @NotBlank(message = "NotBlank", groups = {GroupCreate.class, GroupUpdate.class})
    @NoSpecialCharacterConstraint(message = "NoSpecialCharacter", groups = {GroupCreate.class, GroupUpdate.class})
    private String name;


//    @NotNull(message = "NotNull", groups = {GroupCreate.class, GroupUpdate.class})
//    @NotBlank(message = "NotBlank", groups = {GroupCreate.class, GroupUpdate.class})
    @Size(max = 250, message = "Max250", groups = {GroupCreate.class, GroupUpdate.class})
    private String description;

    @NotNull(message = "NotNull", groups = {GroupCreate.class, GroupUpdate.class})
    @Positive(message = "Positive", groups = {GroupCreate.class, GroupUpdate.class})
    @PositiveIntegerConstraint(message = "PositiveIntegerValidator", groups = {GroupCreate.class, GroupUpdate.class})
    private Double price;

    @Length(message = "Length-5-15", min = 5, max = 15, groups = {GroupCreate.class})  // 5-15 KI TU
    @NotNull(message = "NotNull", groups = {GroupCreate.class})
    @NotBlank(message = "NotBlank", groups = {GroupCreate.class})
    @NoSpecialCharacterConstraint(message = "NoSpecialCharacter", groups = {GroupCreate.class})
    private String productCode;

    @Min(value = 1, message = "Min-1")
    @NotNull(message = "NotNull", groups = {GroupCreate.class, GroupUpdate.class}) // ÍT NHẤT SỐ LƯỢNG LÀ 1
    private Long quantity;

    private String image;

    @NotNull(message = "NotNull", groups = {GroupUpdate.class})
    @Enumerated(EnumType.STRING)
    private EStatus status;

    @ImageFileConstraint(message = "ImageValidate", groups = {GroupCreate.class, GroupUpdate.class})
    @ImageSizeConstraint(maxSize = 2097152, message = "Max2MB", groups = {GroupCreate.class, GroupUpdate.class})
    private MultipartFile file;

    @NotNull(message = "NotNull", groups = {GroupCreate.class, GroupUpdate.class})
    @Size(min = 1, message = "Size-1", groups = {GroupCreate.class, GroupUpdate.class}) // ÍT NHẤT SỐ LƯỢNG LÀ 1
    private List<String> categoryCodes;
}
