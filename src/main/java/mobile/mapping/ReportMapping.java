package mobile.mapping;

import mobile.model.Entity.Comic;
import mobile.model.Entity.Report;
import mobile.model.payload.request.report.ReportRequest;
import mobile.model.payload.response.ReportResponse;
import mobile.Service.ComicService;
import mobile.Service.UserService;
import mobile.model.payload.response.comic.ComicResponse;
import mobile.model.payload.response.user.UserResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class ReportMapping {

    @Autowired
    private ComicService comicService;

    @Autowired
    private UserService userService;

    public Report requestToEntity(ReportRequest request) {
        Report report = new Report();
        report.setComicId(new ObjectId(request.getComicId()));
        report.setUserId(new ObjectId(request.getUserId()));
        report.setReason(request.getReason());
        report.setStatus("PENDING");
        return report;
    }

    public ReportResponse entityToResponse(Report report) {
        ReportResponse response = new ReportResponse();
        response.setId(report.getId().toString());
        response.setComicId(report.getComicId().toString());
        response.setUserId(report.getUserId().toString());
        response.setReason(report.getReason());
        response.setStatus(report.getStatus());

        // Format dates to ISO
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
        response.setCreatedAt(
                report.getCreatedAt() != null ? formatter.format(report.getCreatedAt().toInstant()) : null);
        response.setUpdatedAt(
                report.getUpdatedAt() != null ? formatter.format(report.getUpdatedAt().toInstant()) : null);

        // Fetch comic title with logging
        ComicResponse comic = comicService.findById(report.getComicId());
        System.out.println("Comic for ID " + report.getComicId() + ": " + comic);
        response.setComicTitle(comic != null ? comic.getName() : "Unknown Comic");

        // Fetch username with logging
        UserResponse user = userService.findById(report.getUserId());
        System.out.println("User for ID " + report.getUserId() + ": " + user);
        response.setUsername(user != null ? user.getUsername() : "Anonymous");

        return response;
    }
}