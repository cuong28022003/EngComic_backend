package mobile.businesses.interactors.comment;

import lombok.RequiredArgsConstructor;
import mobile.apis.comment.dtos.CommentResponseDto;
import mobile.apis.user.dtos.UserProfileDto;
import mobile.businesses.boundaries.comment.GetComments;
import mobile.businesses.boundaries.user.GetUserProfile;
import mobile.databases.entities.comment.CommentEntity;
import mobile.databases.repositories.comment.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetCommentsInteractor implements GetComments {

    private final CommentRepository commentRepository;
    private final GetUserProfile getUserProfile;

    @Override
    public Response execute(Request request) {
        if (request.getComicUrl() == null) {
            throw new IllegalArgumentException("Comic URL is required");
        }

        Page<CommentEntity> page = commentRepository.findByComicUrlAndDepth(request.getComicUrl(), 0, request.getPageable());

        List<CommentResponseDto> dtos = page.getContent().stream().map(this::mapCommentRecursive).collect(Collectors.toList());

        return Response.builder()
                .comments(new PageImpl<>(dtos, request.getPageable(), page.getTotalElements()))
                .build();
    }

    private CommentResponseDto mapCommentRecursive(CommentEntity comment) {
        UserProfileDto user = null;
        if (comment.getUserId() != null) {
            try {
                user = getUserProfile.execute(GetUserProfile.Request.builder()
                        .userId(comment.getUserId())
                        .build()).getProfile();
            } catch (Exception ignored) {}
        }

        List<CommentResponseDto> children = Collections.emptyList();
        if (comment.getListChild() != null) {
            children = comment.getListChild().stream().map(this::mapCommentRecursive).collect(Collectors.toList());
        }

        return CommentResponseDto.builder()
                .id(comment.getId())
                .parentId(comment.getParentId())
                .listChild(children)
                .comicUrl(comment.getComicUrl())
                .user(user)
                .numChild(comment.getNumChild())
                .depth(comment.getDepth())
                .content(comment.getContent())
                .createDate(comment.getCreatedate())
                .build();
    }
}
