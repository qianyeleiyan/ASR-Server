package org.codesdream.asr.controller;

import org.codesdream.asr.component.json.model.JsonableTDL;
import org.codesdream.asr.exception.badrequest.IllegalException;
import org.codesdream.asr.exception.notfound.NotFoundException;
import org.codesdream.asr.model.time.TimeDisableLaw;
import org.codesdream.asr.model.user.User;
import org.codesdream.asr.repository.time.TimeDisableLawRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("time")
public class TimeController {

    @Resource
    private TimeDisableLawRepository timeDisableLawRepository;

    @GetMapping("disabled/list")
    @ResponseStatus(HttpStatus.OK)
    JsonableTDL getWeekDayDisabledList(Authentication authentication, @RequestParam(name = "dow") Integer dayOfWeek){
        User user = (User) authentication.getPrincipal();
        if(dayOfWeek > 7 || dayOfWeek < 1) throw new IllegalException(dayOfWeek.toString());
        Optional<TimeDisableLaw> disableLaws =
                timeDisableLawRepository.findAllByUserIdAndDayOfWeek(user.getId(), dayOfWeek);

        if(!disableLaws.isPresent()) throw new NotFoundException(dayOfWeek.toString());

        return new JsonableTDL(disableLaws.get());

    }


    @PostMapping("disabled/set")
    @ResponseStatus(HttpStatus.OK)
    JsonableTDL setDisabledLaw(Authentication authentication, @RequestBody JsonableTDL tdl){
        User user = (User) authentication.getPrincipal();
        if(tdl.getDayOfWeek() > 7 || tdl.getDayOfWeek() < 1)
            throw new IllegalException(tdl.getDayOfWeek().toString());

        Optional<TimeDisableLaw> disableLaws =
                timeDisableLawRepository.findAllByUserIdAndDayOfWeek(user.getId(), tdl.getDayOfWeek());

        disableLaws.ifPresent(timeDisableLaw -> tdl.setId(timeDisableLaw.getId()));

        return new JsonableTDL(timeDisableLawRepository.save(tdl.parseModel(user.getId())));

    }
}
