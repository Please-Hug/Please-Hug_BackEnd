package org.example.hugmeexp.domain.praise.mapper;

import org.example.hugmeexp.domain.praise.dto.CommentRequestDTO;
import org.example.hugmeexp.domain.praise.dto.CommentResponseDTO;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)    // Comment 엔티티의 id 자동 생성되니까 무시 없음
    @Mapping(target = "content", source = "commentRequestDTO.content")
    PraiseComment toEntity(CommentRequestDTO commentRequestDTO, Praise praise, User commentWriter);

    @Mapping(target = "commenterName", expression = "java(comment.getCommentWriter().getName())")
    CommentResponseDTO toDTO(PraiseComment comment);
}
