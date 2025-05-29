package mobile.Service;

import mobile.model.Entity.Comic;
import mobile.model.Entity.Report;
import mobile.model.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportService {
    Page<Report> getAllReports(PageRequest pageRequest);

    Page<Report> getReportsForComic(ObjectId comicId, PageRequest pageRequest);

    List<Report> getReportsByComicId(ObjectId comicId);

    Page<Report> getReportsByStatus(String status, PageRequest pageRequest);

    Report createOrUpdateReport(Report report);

    Report updateReportStatus(ObjectId reportId, String status);
}