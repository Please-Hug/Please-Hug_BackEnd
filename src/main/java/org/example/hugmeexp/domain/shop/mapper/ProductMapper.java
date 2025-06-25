package org.example.hugmeexp.domain.shop.mapper;

import org.example.hugmeexp.domain.shop.dto.ProductResponse;
import org.example.hugmeexp.domain.shop.entity.Product;
import org.example.hugmeexp.domain.shop.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "imageUrl", source = "productImage", qualifiedByName = "toImageUrl")
    ProductResponse toResponse(Product product);

    @Named("toImageUrl")
    static String toImageUrl(ProductImage image) {

        String internalPath = image.getPath();
        String uuid = image.getUuid();
        String ext = image.getExtension();

        String fullPath = internalPath + "/" + uuid + "." + ext;

        // "/application" 제거
        if (fullPath.startsWith("/application")) {
            return fullPath.substring("/application".length());
        }

        return fullPath;
    }
}