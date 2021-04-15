import * as actionTypes from "./actionTypes";
import axios from "axios";


export const loginStart = () => {
    return {
        type: actionTypes.LOGIN_START,
        error: null,
        loading: true
    }
}

export const loginSuccess = (token, role, props) => {
    props.history.push("/")
    return {
        type: actionTypes.LOGIN_SUCCESS,
        token: token,
        role: role,
        error: null,
        loading: false,
    }
}

export const loginFail = (error) => {
    return {
        type: actionTypes.LOGIN_FAIL,
        error: error,
        loading: false
    }
}

export const emailSendFail = (error) => {
    return {
        type: actionTypes.EMAIL_SEND_FAIL,
        error: error,
        msg: null,
        loading: false
    }
}

export const emailSendSuccess = (msg) => {
    return {
        type: actionTypes.EMAIL_SEND_SUCCESS,
        msg: msg,
        error: null,
        loading: false
    }
}

export const changePasswordFail = (error) => {
    return {
        type: actionTypes.CHANGE_PASSWORD_FAIL,
        msg: null,
        error: error,
        loading: false
    }
}

export const changePasswordSuccess = (msg) => {
    return {
        type: actionTypes.CHANGE_PASSWORD_SUCCESS,
        msg: msg,
        error: null,
        loading: false
    }
}

export const verifyFail = (error) => {
    return {
        type: actionTypes.VERIFY_FAIL,
        msg: null,
        error: error,
        loading: false
    }
}

export const verifySuccess = (msg) => {
    return {
        type: actionTypes.VERIFY_SUCCESS,
        msg: msg,
        error: null,
        loading: false
    }
}

export const registerFail = (error) => {
    return {
        type: actionTypes.REGISTER_FAIL,
        msg: null,
        error: error,
        loading: false
    }
}

export const registerSuccess = (msg) => {
    return {
        type: actionTypes.REGISTER_SUCCESS,
        msg: msg,
        error: null,
        loading: false
    }
}

export const login = (email, password, props) => {
    return dispatch => {
        dispatch(loginStart())

        axios({method: "post", url: "/auth/login/", data: {email, password}})
            .then((response) => {
                localStorage.setItem("token", response.data.jwt);
                dispatch(loginSuccess(response.data.jwt, response.data.role, props))
            })
            .catch((e) => {
                dispatch(loginFail("Email or password is not correct."))
            })
    }
}

export const logout = () => {
    localStorage.removeItem("token");

    return {
        type: actionTypes.LOGOUT
    }
}

export const sendForgetPasswordEmail = (email) => {
    return dispatch => {
        axios({method: "get", url: "/auth/forgot_password?email=" + email})
            .then((response) => {
                dispatch(emailSendSuccess("Succesfuly send mail."))
            })
            .catch((e) => {
                dispatch(emailSendFail("Can't send mail to this email."))
            })
    }
}

export const unauthenticatedChangePassword = (token, email, password) => {
    return dispatch => {
        axios.post("/auth/forgot_password", {token, email, 'new-password': password})
            .then((response) => {
                dispatch(changePasswordSuccess("Succesfuly changed password."))
            })
            .catch((e) => {
                dispatch(changePasswordFail("Can't change password."))
            })
    }
}

export const authenticatedChangePassword = (authToken, password) => {
    return dispatch => {
        axios.post("/auth/change_password", {'new-password': password}, {headers: {'Authorization': `Bearer ${authToken}`}})
            .then((response) => {
                dispatch(changePasswordSuccess("Succesfuly changed password."))
            })
            .catch((e) => {
                dispatch(changePasswordFail("Can't change password."))
            })
    }
}

export const verifyAccount = (email, token) => {
    return dispatch => {
        axios.post("/auth/verify", {email, token})
            .then((response) => {
                dispatch(verifySuccess("Succesfuly verified account."))
            })
            .catch((e) => {
                dispatch(verifyFail("Can't verify"))
            })
    }
}

export const registerAccount = (fullname, email, password, age, phoneNumber) => {
    return dispatch => {
        axios.post("/auth/register", {"full-name": fullname, email, password, age, "phone-number": phoneNumber})
            .then((response) => {
                dispatch(registerSuccess("Succesfuly created account. Check your email for verification."))
            })
            .catch((e) => {
                dispatch(registerFail("Can't create account, check entered information."))
            })
    }
}