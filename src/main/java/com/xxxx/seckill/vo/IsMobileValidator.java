package com.xxxx.seckill.vo;

import com.xxxx.seckill.utils.ValidatorUtil;
import com.xxxx.seckill.validator.IsMobile;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidator;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidatorContext;

/**
 * @author yswu
 * @date 2021-02-11 13:59
 */
public class IsMobileValidator implements HibernateConstraintValidator<IsMobile, String> {


    private boolean required = false;
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        boolean required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(required){
            return ValidatorUtil.isMObile(value);

        }else{
            if(StringUtils.isEmpty(value)){
                return true;
            }else{
                return ValidatorUtil.isMObile(value);
            }
        }



    }
}
