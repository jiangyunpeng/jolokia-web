package prj.jolokiaweb.controller;

import org.jolokia.client.J4pClient;
import org.jolokia.client.exception.J4pException;
import org.jolokia.client.request.*;
import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import prj.jolokiaweb.JolokiaApp;
import prj.jolokiaweb.form.ExecForm;
import prj.jolokiaweb.form.ReadForm;
import prj.jolokiaweb.form.WriteForm;

import javax.management.MalformedObjectNameException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


@RestController
public class ApiController {
    @RequestMapping(value = "/api/beans", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> beanTree() {
        JSONObject result = new JSONObject();

        J4pClient j4pClient = new J4pClient(JolokiaApp.getJolokiaUrl());
        try {
            String path = null; // null means full tree
            result = j4pClient.execute(new J4pListRequest(path)).asJSONObject();
        } catch(Exception e) {
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/api/version", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> version() {
        JSONObject result = new JSONObject();

        J4pClient j4pClient = new J4pClient(JolokiaApp.getJolokiaUrl());
        try {
            J4pReadRequest runtimeReq = new J4pReadRequest("java.lang:type=Runtime");
            J4pReadResponse runtimeRes = j4pClient.execute(runtimeReq);
            J4pVersionResponse versionRes = j4pClient.execute(new J4pVersionRequest());

            result.put("runtime", runtimeRes.getValue());
            result.put("version", versionRes.getValue());
        } catch (Exception e) {
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/api/read", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> read(@RequestBody ReadForm readForm) {
        JSONObject result = new JSONObject();

        J4pClient j4pClient = new J4pClient(JolokiaApp.getJolokiaUrl());
        try {
            J4pReadRequest readReq = new J4pReadRequest(readForm.getMbean());
            Map<J4pQueryParameter,String> params = new HashMap<>();;
            params.put(J4pQueryParameter.IGNORE_ERRORS,"true");
            J4pReadResponse readRes = j4pClient.execute(readReq, params);
            result = readRes.getValue();
        } catch (MalformedObjectNameException e) {
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        } catch (J4pException e) {
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/api/execute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> execute(@RequestBody ExecForm execForm) {
        JSONObject result = new JSONObject();

        J4pClient j4pClient = new J4pClient(JolokiaApp.getJolokiaUrl());
        try {
            J4pExecRequest execReq;
            if (execForm.getData() != null && execForm.getData().size() > 0) {
                execReq = new J4pExecRequest(execForm.getMbean(), execForm.getOperation(), execForm.getData().toArray());
            } else {
                execReq = new J4pExecRequest(execForm.getMbean(), execForm.getOperation());
            }
            J4pExecResponse execRes = j4pClient.execute(execReq);
            result = execRes.asJSONObject();
        } catch (MalformedObjectNameException e) {
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        } catch (J4pException e) {
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/api/write", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> write(@RequestBody WriteForm form) {
        JSONObject result = new JSONObject();

        J4pClient j4pClient = new J4pClient(JolokiaApp.getJolokiaUrl());
        try {
            J4pWriteRequest writeReq = new J4pWriteRequest(form.getMbean(), form.getAttribute(), form.getValue());
            J4pWriteResponse writeRes = j4pClient.execute(writeReq);
            result = writeRes.asJSONObject();
        } catch (MalformedObjectNameException e) {
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        } catch (J4pException e) {
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

}
