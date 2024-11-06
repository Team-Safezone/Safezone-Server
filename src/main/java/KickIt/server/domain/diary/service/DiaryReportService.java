package KickIt.server.domain.diary.service;

import KickIt.server.domain.diary.dto.DiaryReportDto;
import KickIt.server.domain.diary.entity.Diary;
import KickIt.server.domain.diary.entity.DiaryReport;
import KickIt.server.domain.diary.entity.DiaryReportRepository;
import KickIt.server.domain.diary.entity.DiaryRepository;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiaryReportService {

    private final DiaryReportRepository diaryReportRepository;
    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;

    @Autowired
    public DiaryReportService(DiaryReportRepository diaryReportRepository, MemberRepository memberRepository, DiaryRepository diaryRepository) {
        this.diaryReportRepository = diaryReportRepository;
        this.memberRepository = memberRepository;
        this.diaryRepository = diaryRepository;
    }

    public boolean saveReport(DiaryReportDto diaryReportDto, String email, Long diaryId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일기입니다."));

        int reasonCode = diaryReportDto.getReasonCode();

        // 중복 확인
        DiaryReport report = diaryReportRepository.getReport(member.getId(), diaryId);

        if (report == null) {
            DiaryReport diaryReport = new DiaryReport();
            diaryReport.setMember(member);
            diaryReport.setDiary(diary);
            diaryReport.setReasonCode(reasonCode);

            diaryReportRepository.save(diaryReport);

            return true;
        } else {
            return false;
        }
    }
}
