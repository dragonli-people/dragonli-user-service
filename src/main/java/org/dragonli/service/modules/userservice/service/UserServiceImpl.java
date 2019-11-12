package org.dragonli.service.modules.userservice.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.dragonli.service.general.interfaces.general.AuthService;
import org.dragonli.service.general.interfaces.general.OtherService;
import org.dragonli.service.modules.user.interfaces.UserService;
import org.dragonli.service.modules.userservice.ErrorCode;
import org.dragonli.service.modules.userservice.entity.enums.UserStatus;
import org.dragonli.service.modules.userservice.entity.models.UserEntity;
import org.dragonli.service.modules.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service(interfaceClass = UserService.class, register = true, timeout = 150000000, retries = -1, delay = -1)
public class UserServiceImpl implements UserService {
    final Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${USER_NAME_REG_STR}")
    String usernameRegStr;
    @Value("${PASSWD_REG_STR}")
    String passwdRegStr;
    @Value("${service.micro-service.user-service.validate-code-out-time:180000}")
    Long validateCodeOutTime;
    @Value("${service.micro-service.user-service.email-code-out-time:180000}")
    Long emailCodeOutTime;
    @Value("${service.micro-service.user-service.phone-code-out-time:180000}")
    Long phoneCodeOutTime;
    @Value("${service.micro-service.user-service.login-code-out-time:60000}")
    Long loginCodeOutTime;
    protected final static Random random = new Random();
    @Autowired
    UserRepository userRepository;
    @Reference
    AuthService authService;
    @Reference
    OtherService otherService;
    @Autowired
    private Environment environment;
    private final String CHAINX_SERVICE_PAUSING = "CHAINX_SERVICE_PAUSING";
    private final String CHAINX_SERVICE_PAUSING_INFO = "CHAINX_SERVICE_PAUSING_INFO";
    static final Pattern PATTERN_EMAIL = Pattern.compile(
            "^[_a-zA-Z0-9-\\\\+]+(\\.[_a-zA-Z0-9-]+)*@[\\.0-9A-z]+((.com)|(.net)|(.com.cn)|(.cn)|(.COM)|(.NET)|(.COM" +
                    ".CN)|(.CN))+$");
    static final Pattern PATTERN_PHONE = Pattern.compile("^\\d+$");

    @Transactional
    @Override
    public Map<String, Object> registUser(String username, String password) throws Exception {
        return registUser(username, password, null, null, null, null, null, null, null);
    }

    @Transactional
    @Override
    public Map<String, Object> registUser(String username, String password, String recommendCode,
            Long countryId) throws Exception {
        return registUser(username, password, recommendCode, countryId, null, null, null, null, null);
    }

    @Transactional
    @Override
    public Map<String, Object> registUser(String username, String password, String recommendCode, Long countryId,
            String email, String phone, String nickname, String passwdCode,
            Map<String, Object> expendsParas) throws Exception {

        JSONObject result = new JSONObject();
        ErrorCode errorCode = null;
        if (null == username || null == password || "".equals(username = username.trim()) ||
                "".equals(password = password.trim())) errorCode = ErrorCode.ONE_OF_PARAS_IS_NULL;
        if (errorCode == null && null != userRepository.findByUsername(username)) errorCode = ErrorCode.USERNAME_REPEAT;
        if (errorCode == null && null != userRepository.findByPhone(username)) errorCode = ErrorCode.USERNAME_REPEAT;
        if (errorCode == null && null != userRepository.findByEmail(username)) errorCode = ErrorCode.USERNAME_REPEAT;
        if (errorCode == null && null != email && !"".equals(email = email.trim()) &&
                null != userRepository.findByUsername(username)) errorCode = ErrorCode.USERNAME_REPEAT;
        if (errorCode == null && null != phone && !"".equals(phone = phone.trim()) &&
                null != userRepository.findByPhone(phone)) errorCode = ErrorCode.PHONE_REPEAT;
        if (errorCode == null && null != email && !"".equals(email = email.trim()) &&
                null != userRepository.findByEmail(email)) errorCode = ErrorCode.EMAIL_REPEAT;
        if (errorCode == null && null != email && !"".equals(email = email.trim()) &&
                !PATTERN_EMAIL.matcher(email).matches()) errorCode = ErrorCode.EMAIL_FORMAT_ERROR;
        if (errorCode == null && null != phone && !"".equals(phone = phone.trim()) &&
                !PATTERN_PHONE.matcher(phone).matches()) errorCode = ErrorCode.PHONE_FORMAT_ERROR;
        if (errorCode == null && !Pattern.compile(usernameRegStr).matcher(username).matches())
            errorCode = ErrorCode.USERNAME_FORMAT_ERROR;
        if (errorCode == null && !Pattern.compile(passwdRegStr).matcher(password).matches())
            errorCode = ErrorCode.PASSWD_FORMAT_ERROR;

        if (errorCode != null) return createErrorResult(errorCode, result);

        String newpw = otherService.sha1(password);

        if (nickname == null || "".equals(nickname = nickname.trim())) nickname = username;
        if (passwdCode == null || "".equals(passwdCode = passwdCode.trim())) passwdCode = newpw;
        if (countryId == null || countryId <= 0) countryId = 1L;//todo
        if (recommendCode == null) recommendCode = "";

        UserEntity u = new UserEntity();
        u.setUsername(username);
        u.setNickname(nickname);
        u.setPasswd(newpw);
        u.setPasswdCode(passwdCode);
        u.setPhone(phone);
        u.setEmail(email);
        u.setStatus(UserStatus.ACTIVE);
        u.setPhoneCode("");
        u.setRegistTime(System.currentTimeMillis());
        u.setRecommendCode(recommendCode);
        u.setCountryId(countryId);
        u.setPhoneValidated(false);
        u.setEmailValidated(false);

        u = userRepository.save(u);

        logger.info("user regist success : " + username);

        result.put("result", true);
        result.put("data", JSON.toJSON(u));
        result.put("userId", u.getId());
        return result;
    }

    @Transactional
    @Override
    public Map<String, Object> login(String username, String encryptedPasswd) throws Exception {

        JSONObject result = new JSONObject();
        Object findResult = findUserByUsernameOrEmailOrPhone(username);
        if (findResult instanceof ErrorCode) return createErrorResult((ErrorCode) findResult, result);
        ErrorCode errorCode = null;
        UserEntity u = (UserEntity) findResult;
        if (null == encryptedPasswd || "".equals(encryptedPasswd = encryptedPasswd.trim())) errorCode = ErrorCode.ONE_OF_PARAS_IS_NULL;
        String npw = otherService.sha1(encryptedPasswd);
        if (errorCode == null && !u.getPasswd().equals(npw)) errorCode = ErrorCode.PASSWD_ERROR;
        if (errorCode != null) return createErrorResult(ErrorCode.PASSWD_ERROR, result);

        result.put("result", true);
        result.put("data", JSON.toJSON(u));
        return result;
    }

    @Transactional
    @Override
    public Map<String, Object> loginByCode(String username, String code) throws Exception{
        Object findResult = findUserByUsernameOrEmailOrPhone(username);
        if (findResult instanceof ErrorCode) return createErrorResult((ErrorCode) findResult);
        UserEntity u = (UserEntity) findResult;
        if(u.getLoginCode()==null)return createErrorResult(ErrorCode.NO_CODE);
        if(System.currentTimeMillis() > Long.parseLong( u.getLoginCode().split("\\|")[1] ) )
            return createErrorResult(ErrorCode.CODE_TIME_OUT);
        if(code == null || !u.getLoginCode().split("\\|")[0].equals(code))
            return createErrorResult(ErrorCode.CODE_ERROR);
        u.setLoginCode(null);
        u = userRepository.save(u);
        JSONObject result = new JSONObject();
        result.put("result", true);
        result.put("data", JSON.toJSON(u));
        return result;
    }

    @Transactional
    @Override
    public Map<String, Object> getUserById(Long id){
        return (JSONObject)JSON.toJSON( userRepository.get(id) );
    }
    @Transactional
    @Override
    public List<Map<String, Object>> getUserList(List<Long> idList){
        return userRepository.findByIdIn(idList).stream().map(v->(JSONObject)JSON.toJSON(v)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Map<String, Object> findUser(Object key) {
        if (key == null) return null;
        UserEntity u = null;
        if (key instanceof Long) return JSONObject.parseObject(JSON.toJSONString(userRepository.get((Long) key)));
        if (key instanceof String)
            return JSONObject.parseObject(JSON.toJSONString(findUserByUsernameOrEmailOrPhone((String) key)));

        return null;
    }

    protected Object findUserByUsernameOrEmailOrPhone(String username) {
        ErrorCode errorCode = null;
        UserEntity u = null;
        if (null == username || "".equals(username = username.trim())) errorCode = ErrorCode.ONE_OF_PARAS_IS_NULL;
        boolean isMailFormat = PATTERN_EMAIL.matcher(username).matches();
        boolean isPhoneFormat = PATTERN_PHONE.matcher(username).matches();
        if (null == errorCode && u == null && isMailFormat) u = userRepository.findByEmail(username);
        if (null == errorCode && u == null && isPhoneFormat) u = userRepository.findByPhone(username);
        if (null == errorCode && u == null) u = userRepository.findByUsername(username);
        if (null == u) return ErrorCode.USERNAME_NOT_EXSIT;
        return u;
    }

    @Override
    @Transactional
    public Map<String, Object> resetPasswdById(Long id, String code) throws Exception {
        UserEntity u = userRepository.get(id);
        if (u == null) return createErrorResult(ErrorCode.USER_ID_NOT_EXSIT);
        return resetPasswd(u, code, null);
    }

    @Override
    @Transactional
    public Map<String, Object> resetPasswdByKey(String key, String code) throws Exception {
        Object findResult = findUserByUsernameOrEmailOrPhone(key);
        if (!(findResult instanceof ErrorCode)) return createErrorResult((ErrorCode) findResult);
        return resetPasswd((UserEntity) findResult, code, null);
    }

    protected Map<String, Object> resetPasswd(UserEntity u, String code, String passwdCode) throws Exception {
        JSONObject result = new JSONObject();
        ErrorCode errorCode = null;
        if (null == code || "".equals(code = code.trim())) errorCode = ErrorCode.ONE_OF_PARAS_IS_NULL;
        if (u.getValidataCode() == null) errorCode = ErrorCode.NO_RESET_CODE;
        int codeLen = u.getVersion() != null ? Integer.parseInt(u.getValidataCode().split("\\|")[1]) : 0;
        if (errorCode != null && code.length() != codeLen) errorCode = ErrorCode.PASSWD_ERROR;
        if (errorCode != null && code.equals(otherService.sha1(u.getPasswd() + u.getValidataCode(), codeLen)))
            errorCode = ErrorCode.PASSWD_ERROR;
        if (System.currentTimeMillis() > Long.parseLong(u.getValidataCode().split("\\|")[0]))
            errorCode = ErrorCode.CODE_TIME_OUT;
        if (errorCode != null) return createErrorResult(errorCode, result);

        String newPasswd = otherService.sha1(code);
        if (passwdCode == null || "".equals(passwdCode = passwdCode.trim())) passwdCode = newPasswd;
        u.setValidataCode(null);
        u.setPasswd(newPasswd);
        u.setPasswdCode(passwdCode);
        u = userRepository.save(u);
        result.put("result", true);
        result.put("data", JSON.toJSON(u));
        result.put("userId", u.getId().toString());
        return result;
    }

    @Transactional
    @Override
    public Map<String, Object> changePassword(Long id, String password, String newpw, String passwdCode,
            Boolean dontValicodeOld) throws Exception {

        UserEntity u = userRepository.get(id);
        if (u == null) return createErrorResult(ErrorCode.USER_ID_NOT_EXSIT);

        ErrorCode errorCode = null;
        JSONObject result = new JSONObject();
        if (newpw == null || "".equals(newpw = newpw.trim()) ||
                !Pattern.compile(passwdRegStr).matcher(password).matches())
            return createErrorResult(ErrorCode.PASSWD_FORMAT_ERROR);

        dontValicodeOld = dontValicodeOld == null ? false : dontValicodeOld;

        if (password != null) password = otherService.sha1(password);
        if (dontValicodeOld && password == null) password = u.getPasswd();
        if (password == null || !password.equals(u.getPasswd())) {
            result.put("result", false);
            result.put("errCode", ErrorCode.PASSWD_ERROR);
            return result;
        }

        String npw = otherService.sha1(newpw);
        if (passwdCode == null) passwdCode = npw;
        u.setPasswd(npw);
        u.setPasswdCode(passwdCode);
        userRepository.save(u);
        result.put("result", true);
        result.put("data", JSON.toJSON(u));
        return result;
    }

    /*
    @Transactional
    @Override
    public Map<String, Object> changeUserName(Map<String, Object> jsonParams) throws Exception {
        String password = (String) jsonParams.get("password");
        String newUser = (String) jsonParams.get("newUser");
        String username = (String) jsonParams.get("username");
        Map<String, Object> result = new HashMap<>();
        if (null == username || null == password || null == newUser) {
            result.put("result", false);
            result.put("errCode", "PARAS_IS_NULL");
            result.put("data", "");
            return result;
        }
        UserEntity u = userRepository.findByUsername(username);
        if (u == null) {
            result.put("result", false);
            result.put("errCode", "USER_NOT_EXITS");
            result.put("data", "");
            return result;
        }
        UserEntity u1 = userRepository.findByUsername(newUser);
        if (u1 != null) {
            result.put("result", false);
            result.put("errCode", "USERNAME_REPEAT");
            result.put("data", "");
            return result;
        }
        u.setUsername(newUser);
        result.put("result", true);
        result.put("data", JSON.toJSON(u));
        return result;
    }
    */

    @Transactional
    @Override
    public Map<String, Object> changeEmail(Long id, String code, String newEmail, Boolean setEmailValidated) throws Exception {

        UserEntity u = userRepository.get(id);
        if (u == null) return createErrorResult(ErrorCode.USER_ID_NOT_EXSIT);

        JSONObject result = new JSONObject();
        ErrorCode errorCode = null;

        boolean dontValidate = newEmail != null && "".equals(newEmail = newEmail.trim());
        if(dontValidate && !PATTERN_EMAIL.matcher(newEmail).matches())
            return createErrorResult(ErrorCode.EMAIL_FORMAT_ERROR,result);
        if (dontValidate)
        {
            doResetEmail(u, newEmail, result,setEmailValidated);
            return result;
        }

        if (u.getEmailCode() == null) return createErrorResult(ErrorCode.NO_CODE,result);
        newEmail = u.getEmailCode().split("\\|")[2];
        if (errorCode != null && (u.getEmailCode() == null || !u.getEmailCode().split("\\|")[0].equals(code)))
            errorCode = ErrorCode.CODE_ERROR;
        if (errorCode != null && System.currentTimeMillis() > Long.parseLong(u.getEmailCode().split("\\|")[1]))
            errorCode = ErrorCode.CODE_TIME_OUT;

        if (errorCode != null)  return createErrorResult(errorCode,result);

        doResetEmail(u, newEmail, result,setEmailValidated);
        return result;
    }

    protected void doResetEmail(UserEntity u, String email, JSONObject result,Boolean setEmailValidated) {
        u.setEmail(email);
        u.setEmailCode(null);
        if(setEmailValidated != null && setEmailValidated) u.setEmailValidated(true);
        u = userRepository.save(u);
        result.put("result", true);
        result.put("data", JSON.toJSON(u));
    }

    @Transactional
    @Override
    public Map<String, Object> changePhone(Long id, String code, String newPhone, Boolean setPhoneValidated) throws Exception {

        UserEntity u = userRepository.get(id);
        if ( null == u ) return createErrorResult(ErrorCode.USER_ID_NOT_EXSIT);


        boolean dontValidate = newPhone != null && "".equals(newPhone = newPhone.trim());
        if(dontValidate && !PATTERN_PHONE.matcher(newPhone).matches())
            return createErrorResult(ErrorCode.PHONE_FORMAT_ERROR);

        ErrorCode errorCode = null;
        JSONObject result = new JSONObject();
        if(dontValidate){
            doResetPhone(u, newPhone, result,setPhoneValidated);
            return result;
        }

        if (u.getPhoneCode() == null) createErrorResult(ErrorCode.NO_CODE,result);
        newPhone = u.getPhoneCode().split("\\|")[2];
        if (errorCode != null &&  !u.getPhoneCode().split("\\|")[0].equals(code))
            errorCode = ErrorCode.CODE_ERROR;
        if (newPhone == null && System.currentTimeMillis() > Long.parseLong(u.getPhoneCode().split("\\|")[1]))
            errorCode = ErrorCode.CODE_TIME_OUT;

        if (errorCode != null) return createErrorResult(errorCode,result);

        doResetPhone(u, newPhone, result,setPhoneValidated);
        return result;
    }

    protected void doResetPhone(UserEntity u, String phone, JSONObject result,Boolean setPhoneValidated) {
        u.setPhone(phone);
        u.setPhoneCode(null);
        if(setPhoneValidated != null && setPhoneValidated) u.setPhoneValidated(true);
        u = userRepository.save(u);
        result.put("result", true);
        result.put("data", JSON.toJSON(u));
    }

    @Override
    @Transactional
    public JSONObject generateValidateCodeByUserId(Long id) throws Exception {
        UserEntity u = userRepository.get(id);
        if (u == null) return createErrorResult(ErrorCode.USER_ID_NOT_EXSIT);
        return doGenerateValidateCode(u);
    }

    @Override
    @Transactional
    public JSONObject generateValidateCodeByUserId(String key) throws Exception {
        Object findResult = findUserByUsernameOrEmailOrPhone(key);
        if (!(findResult instanceof ErrorCode)) return createErrorResult((ErrorCode) findResult);
        return doGenerateValidateCode((UserEntity) findResult);
    }

    protected JSONObject doGenerateValidateCode(UserEntity u) throws Exception {
        JSONObject result = new JSONObject();
        Long outTime = System.currentTimeMillis() + validateCodeOutTime;
        Integer codeLen = 12;
        byte[] bytes = new byte[8];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = generateCode(65,97).byteValue();
        String code = new String(bytes);
        u.setValidataCode(outTime.toString() + "|" + codeLen.toString() + "|" + code);
        u = userRepository.save(u);

        result.put("code", otherService.sha1(u.getValidataCode(), codeLen) );
        result.put("result",true);
        return result;
    }

    @Transactional
    protected JSONObject generateEmailCode(UserEntity u,String newEmail) throws Exception {
        if(newEmail == null || "".equals(newEmail=newEmail.trim())
                || !PATTERN_EMAIL.matcher(newEmail).matches())
            return createErrorResult(ErrorCode.EMAIL_FORMAT_ERROR);

        JSONObject result = new JSONObject();
        String code = generateCode(1000,9999).toString();
        Long outTime = System.currentTimeMillis() + emailCodeOutTime;
        u.setEmailCode(code + "|" + outTime.toString() + "|" + newEmail );
        userRepository.save(u);

        result.put("result",true);
        result.put("code", code );
        return result;
    }

    @Transactional
    protected JSONObject generatePhoneCode(UserEntity u,String phone) throws Exception {
        if(phone == null || "".equals(phone=phone.trim())
                || !PATTERN_PHONE.matcher(phone).matches())
            return createErrorResult(ErrorCode.PHONE_FORMAT_ERROR);

        JSONObject result = new JSONObject();
        String code = generateCode(1000,9999).toString();
        Long outTime = System.currentTimeMillis() + phoneCodeOutTime;
        u.setPhoneCode(code + "|" + outTime.toString() + "|" + phone );
        userRepository.save(u);

        result.put("code", code );
        result.put("result",true);
        return result;
    }

    @Override
    @Transactional
    public JSONObject generateLoginCode(String key){
        Object findResult = findUserByUsernameOrEmailOrPhone(key);
        if( findResult instanceof  ErrorCode ) return createErrorResult((ErrorCode)findResult);
        UserEntity u = (UserEntity)findResult;
        JSONObject result = new JSONObject();
        String code = generateCode(1000,9999).toString();
        u.setLoginCode(code+"|"+(System.currentTimeMillis()+loginCodeOutTime));
        userRepository.save(u);

        result.put("code", code );
        result.put("result",true);
        return result;
    }

    protected JSONObject createErrorResult(ErrorCode errorCode) {
        return createErrorResult(errorCode, null);
    }

    protected JSONObject createErrorResult(ErrorCode errorCode, JSONObject result) {
        if (null == result) result = new JSONObject();
        result.put("result", false);
        result.put("errCode", errorCode.name());
        return result;
    }

    protected Integer generateCode(int base,int max){
        return random.nextInt(max-base+1);
    }

}
