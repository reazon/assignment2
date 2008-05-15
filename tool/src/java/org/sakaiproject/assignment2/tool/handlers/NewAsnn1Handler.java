package org.sakaiproject.assignment2.tool.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.assignment2.logic.AssignmentLogic;
import org.sakaiproject.assignment2.model.Assignment2;

public class NewAsnn1Handler extends Asnn2HandlerBase
{
	private AssignmentLogic assnLogic;

	@Override
	public void postInit(Map<String, String> config) throws ServletException
	{
		assnLogic = (AssignmentLogic) getService(AssignmentLogic.class);
	}

	@Override
	public void handleGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		String id = request.getParameter("id");
		if (id != null)
		{
			Assignment2 assn = assnLogic.getAssignmentById(Long.parseLong(id));
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("title", assn.getTitle());
			map.put("instructions", assn.getInstructions());
	
			sendMap(request, response, map);
		}
	}

	@Override
	public void handlePost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		String id = request.getParameter("id");
		String context = request.getParameter("context");
		String title = request.getParameter("title");
		String instructions = request.getParameter("instructions");
		String draft = request.getParameter("draft");

		Assignment2 assn = new Assignment2();
		if (id != null && id.length() > 0)
			assn.setId(Long.parseLong(id));
		assn.setContextId(context);
		assn.setTitle(title);
		assn.setInstructions(instructions);

		String next = "/sakai-assignment2-tool/sdata/newassignment2.html?id=" + assn.getId();
		if (draft != null)
		{
			assn.setDraft(true);
			next = "/sakai-assignment2-tool/content/templates/close.html";
		}

		assnLogic.saveAssignment(assn);

		response.sendRedirect(next);
	}
}
