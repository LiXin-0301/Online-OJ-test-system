package com.oj.dhuoj.judge;

import com.oj.dhuoj.model.entity.QuestionSubmit;
import com.oj.dhuoj.judge.strategy.DefaultJudgeStrategy;
import com.oj.dhuoj.judge.strategy.JavaLanguageJudgeStrategy;
import com.oj.dhuoj.judge.strategy.JudgeContext;
import com.oj.dhuoj.judge.strategy.JudgeStrategy;
import com.oj.dhuoj.judge.codesandbox.model.JudgeInfo;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
