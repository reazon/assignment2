package org.sakaiproject.assignment2.tool.handlers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.assignment2.logic.AssignmentLogic;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.sdata.tool.json.JSONServiceHandler;

public class NewAssn2Handler extends JSONServiceHandler
{
	private ComponentManager compMgr;
	private AssignmentLogic assnLogic;
	private DateFormat dateFormat;

	@Override
	public void init(Map<String, String> config) throws ServletException
	{
		compMgr = org.sakaiproject.component.cover.ComponentManager.getInstance();
		assnLogic = (AssignmentLogic) compMgr.get(AssignmentLogic.class.getName());
		dateFormat = new SimpleDateFormat("MM/dd");
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		String id = request.getParameter("id");
		Assignment2 assn = assnLogic.getAssignmentById(Long.parseLong(id));
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("submissionType", assn.getSubmissionType());
		map.put("openDate", dateFormat.format(assn.getInstructions()));
		map.put("dueDate", dateFormat.format(assn.getDueDate()));
		map.put("acceptUntil", dateFormat.format(assn.getAcceptUntilTime()));
		map.put("whoWillSubmit", "");
		map.put("grading", "");
		sendMap(request, response, map);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		String id = request.getParameter("id");
		String submissionType = request.getParameter("submissionType");
		String openDate = request.getParameter("openDate");
		String dueDate = request.getParameter("dueDate");
		String acceptUntil = request.getParameter("acceptUntil");
		String whoWillSubmit = request.getParameter("whoWillSubmit");
		String grading = request.getParameter("grading");

		Assignment2 assn = assnLogic.getAssignmentById(Long.parseLong(id));
		assn.setSubmissionType(Integer.parseInt(submissionType));
		assn.setOpenTime(dateFormat.parse(openDate));
		assn.setDueDate(dateFormat.parse(dueDate));
		assn.setAcceptUntilTime(dateFormat.parse(acceptUntil));

		assnLogic.saveAssignment(assn);

		response.sendRedirect("newAssn2?id=" + assn.getId());
	}
}
