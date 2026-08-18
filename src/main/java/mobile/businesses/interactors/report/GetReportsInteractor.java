package mobile.businesses.interactors.report;

import lombok.RequiredArgsConstructor;
import mobile.apis.comic.dtos.ComicResponseDto;
import mobile.apis.report.dtos.ReportResponseDto;
import mobile.apis.user.dtos.UserProfileDto;
import mobile.businesses.boundaries.comic.GetComicDetail;
import mobile.businesses.boundaries.report.GetReports;
import mobile.businesses.boundaries.user.GetUserProfile;
import mobile.databases.entities.report.ReportEntity;
import mobile.databases.repositories.report.ReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetReportsInteractor implements GetReports {

    private final ReportRepository reportRepository;
    private final GetComicDetail getComicDetail;
    private final GetUserProfile getUserProfile;

    @Override
    public Response execute(Request request) {
        Page<ReportEntity> page;
        if (request.getStatus() != null && !request.getStatus().equalsIgnoreCase("None")) {
            page = reportRepository.findByStatus(request.getStatus(), request.getPageable());
        } else {
            page = reportRepository.findAll(request.getPageable());
        }

        List<ReportResponseDto> dtos = page.getContent().stream().map(report -> {
            ComicResponseDto comic = null;
            UserProfileDto user = null;
            if (report.getComicId() != null) {
                try {
                    comic = getComicDetail.execute(GetComicDetail.Request.builder().id(report.getComicId()).build()).getComic();
                } catch (Exception ignored) {}
            }
            if (report.getUserId() != null) {
                try {
                    user = getUserProfile.execute(GetUserProfile.Request.builder().userId(report.getUserId()).build()).getProfile();
                } catch (Exception ignored) {}
            }
            return ReportResponseDto.builder()
                    .id(report.getId())
                    .comicId(report.getComicId())
                    .userId(report.getUserId())
                    .reason(report.getReason())
                    .status(report.getStatus())
                    .createdAt(report.getCreatedAt())
                    .updatedAt(report.getUpdatedAt())
                    .comic(comic)
                    .user(user)
                    .build();
        }).collect(Collectors.toList());

        return Response.builder()
                .reports(new PageImpl<>(dtos, request.getPageable(), page.getTotalElements()))
                .build();
    }
}
