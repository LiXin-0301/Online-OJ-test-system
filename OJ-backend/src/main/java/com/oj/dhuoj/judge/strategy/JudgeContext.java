package com.oj.dhuoj.judge.strategy;

import com.oj.dhuoj.model.dto.question.JudgeCase;
import com.oj.dhuoj.model.entity.Question;
import com.oj.dhuoj.model.entity.QuestionSubmit;
import com.oj.dhuoj.judge.codesandbox.model.JudgeInfo;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
