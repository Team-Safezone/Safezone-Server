package KickIt.server.domain.teams.controller;

import KickIt.server.domain.teams.dto.EplTeamDto;
import KickIt.server.domain.teams.service.EplTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/eplTeam")
public class EplTeamController {
    @Autowired
    private EplTeamService eplTeamService;

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllEplTeams(){
        Map<String, Object> responseBody = new HashMap<>();
        List<EplTeamDto.EplTeamResponse> responseList;
        responseList = eplTeamService.FindAllEplTeams();

        // 조회해 가져온 데이터가 존재하는 경우 성공, OK status로 반환
        if(!responseList.isEmpty()){
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("data", responseList);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
        // 조회한 list가 비어있는 경우 데이터 없음 처리, NOT FOUND로 반환
        else{
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("message", "데이터 없음");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }
}
