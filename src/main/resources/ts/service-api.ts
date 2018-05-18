export enum RequestMethod {
    GET = "GET", HEAD = "HEAD", POST = "POST", PUT = "PUT", PATCH = "PATCH", DELETE = "DELETE", OPTIONS = "OPTIONS", TRACE = "TRACE"
}

export interface HttpRequestMapping {
    urlTemplate: string;
    method: RequestMethod;
}

export interface MethodParamMapping {
    paramName: string;
    isRequired: boolean;
    pathVariableName?: string;
    requestParameterName?: string;
    isRequestBody: boolean;
    value: any;
}



