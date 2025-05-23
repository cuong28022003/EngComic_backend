package mobile.mapping;

import mobile.model.Entity.Comment;
import mobile.model.Entity.User;
import mobile.model.payload.request.comment.CommentRequest;
import mobile.model.payload.response.CommentResponse;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class CommentMapping {




    public static Comment CommentRequestToEntity(CommentRequest commentRequest, User user) {
        ObjectId idParent = null;
        if(commentRequest.getParentId()!= null && !commentRequest.getParentId().isEmpty())
            idParent = new ObjectId(commentRequest.getParentId());
        return new Comment(commentRequest.getUrl(),user,idParent,commentRequest.getContent());
    }

    public static CommentResponse EntityToResponse(Comment comment) {

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId().toString());
        if(comment.getParentId() !=null)
            commentResponse.setParentId(comment.getParentId().toHexString());
        else
            commentResponse.setParentId(null);
        commentResponse.setUrltruyen(comment.getComicUrl());
        commentResponse.setContent(comment.getContent());
        commentResponse.setNumChild(comment.getNumChild());
        commentResponse.setDepth(comment.getDepth());
        commentResponse.setTenhienthi(comment.getUser().getFullName());
        commentResponse.setUsername(comment.getUser().getUsername());
        commentResponse.setImage(comment.getUser().getImage());
        commentResponse.setDate(comment.getCreatedate());
        for(Comment temp : comment.getListChild()){
            commentResponse.getListChild().add(EntityToResponse(temp));
        }
        return commentResponse;
    }
    public static List<CommentResponse> ListEntityToResponse(List<Comment> comments) {
        List<CommentResponse> responseList = new ArrayList<>();
        for(Comment temp : comments){
            responseList.add(EntityToResponse(temp));
        }
        return responseList;
    }
}
