package top.naccl.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.naccl.entity.Moment;
import top.naccl.entity.User;
import top.naccl.model.vo.PageResult;
import top.naccl.model.vo.Result;
import top.naccl.service.MomentService;
import top.naccl.service.impl.UserServiceImpl;
import top.naccl.util.JwtUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 动态
 * @Author: Naccl
 * @Date: 2020-08-25
 */
@RestController
public class MomentController {
	@Autowired
	MomentService momentService;
	@Autowired
	UserServiceImpl userService;

	/**
	 * 分页查询动态List
	 *
	 * @param pageNum 页码
	 * @return
	 */
	@GetMapping("/moments")
	public Result moments(@RequestParam(defaultValue = "1") Integer pageNum, HttpServletRequest request) {
		boolean adminIdentity = false;
		String jwtToken = request.getHeader("Authorization");
		if (jwtToken != null && !"".equals(jwtToken) && !"null".equals(jwtToken)) {
			try {
				String subject = JwtUtils.validateToken(jwtToken);
				if (subject.startsWith("admin:")) {//博主身份Token
					String username = subject.replace("admin:", "");
					User admin = (User) userService.loadUserByUsername(username);
					if (admin != null) {
						adminIdentity = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		PageInfo<Moment> pageInfo = new PageInfo<>(momentService.getMomentVOList(pageNum, adminIdentity));
		PageResult<Moment> pageResult = new PageResult<>(pageInfo.getPages(), pageInfo.getList());
		return Result.ok("获取成功", pageResult);
	}

	/**
	 * 给动态点赞
	 *
	 * @param id 动态id
	 * @return
	 */
	@PostMapping("/moment/like")
	public Result like(@RequestParam Long id) {
		momentService.addLikeByMomentId(id);
		return Result.ok("点赞成功");
	}
}
