package mobile.Service.Impl;

import mobile.Handler.RecordNotFoundException;
import mobile.model.Entity.Report;
import mobile.repository.ReportRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import mobile.Service.*;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Override
    public Page<Report> getAllReports(PageRequest pageRequest) {
        return reportRepository.findAll(pageRequest);
    }

    @Override
    public Page<Report> getReportsForComic(ObjectId comicId, PageRequest pageRequest) {
        return reportRepository.findByComicId(comicId, pageRequest);
    }

    @Override
    public List<Report> getReportsByComicId(ObjectId comicId) {
        return reportRepository.findAllByComicId(comicId);
    }

    @Override
    public Page<Report> getReportsByStatus(String status, PageRequest pageRequest) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status value");
        }
        return reportRepository.findByStatus(status, pageRequest);
    }

    @Override
    public Report createOrUpdateReport(Report report) {
        if (report.getStatus() == null) {
            report.setStatus("PENDING"); // Default status for new reports
        } else if (!isValidStatus(report.getStatus())) {
            throw new IllegalArgumentException("Invalid status value");
        }
        return reportRepository.save(report);
    }

    @Override
    public Report updateReportStatus(ObjectId reportId, String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status value");
        }
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RecordNotFoundException("Report not found with id: " + reportId));
        report.setStatus(status);
        return reportRepository.save(report);
    }

    private boolean isValidStatus(String status) {
        return status != null && (status.equals("PENDING") ||
                status.equals("APPROVED") ||
                status.equals("REJECTED") ||
                status.equals("RESOLVED"));
    }
}