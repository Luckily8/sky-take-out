package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自动填充公共字段切面
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点,匹配 com.sky.mapper 包下的所有方法并且要求有 @AutoFill 注解
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知,在方法执行之前填充公共字段
     */
    @Before("autoFillPointCut()")
    public void autoFillBefore(JoinPoint joinPoint){
        log.info("开始填充公共字段...");
        // 获取被拦截的方法对应的数据库操作类型
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature(); // 获取方法签名
        AutoFill annotation = methodSignature.getMethod().getAnnotation(AutoFill.class);// 获取方法上的 @AutoFill 注解
        OperationType operationType = annotation.value(); // 获取操作类型
        // 获取方法参数即传入的实体对象
        Object[] args = joinPoint.getArgs();
        if (args.length == 0 || args[0] == null) {
            log.error("方法参数为空");
            return;
        }
        Object entity = args[0]; // 获取第一个参数,即实体对象(默认传入的第一个参数是实体对象)
        // 准备赋值的数据
        Long currentId = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();

        // 跟据操作类型进行反射赋值,根据实体对象反射出相应的setter方法
        Class<?> aClass = entity.getClass();
        if (operationType == OperationType.INSERT) {
            // 如果是插入操作,则赋值创建时间和创建人,更新人和更新时间
            // 反射出setter方法
            try {
                Method setCreateTime = aClass.getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setUpdateTime = aClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = aClass.getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = aClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 赋值
                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }else if (operationType == OperationType.UPDATE) {
            // 如果是更新操作,则赋值更新人和更新时间
            try {
                Method setUpdateTime = aClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = aClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 赋值
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

}
