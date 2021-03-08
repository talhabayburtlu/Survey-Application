import * as actionTypes from "../actions/actionTypes";
import {updateObject} from "../utility";

const initialState = {
    token: localStorage.getItem("token"),
    isAuthenticated: localStorage.getItem("token") !== null,
    role: null,
    error: null,
    loading: false,
    mgs: null
}

const loginStart = (state, action) => {
    return updateObject(state, {error: false, loading: true})
}

const loginSuccess = (state, action) => {
    return updateObject(state, {
        token: action.token,
        role: action.role,
        error: null,
        isAuthenticated: true,
        loading: false,
    })
}

const loginFail = (state, action) => {
    return updateObject(state, {
        error: action.error,
        loading: false
    })
}

const logout = (state, action) => {
    return updateObject(state, {
        isAuthenticated: false,
        token: null,
        role: null
    })
}

const emailSendFail = (state, action) => {
    return updateObject(state, {
        error: action.error,
        msg: null,
        loading: false
    })
}

const emailSendSuccess = (state, action) => {
    return updateObject(state, {
        error: null,
        msg: action.msg,
        loading: false
    })
}

const changePasswordFail = (state, action) => {
    return updateObject(state, {
        error: action.error,
        msg: null,
        loading: false
    })
}

const changePasswordSuccess = (state, action) => {
    return updateObject(state, {
        error: null,
        msg: action.msg,
        loading: false
    })
}


const verifyFail = (state, action) => {
    return updateObject(state, {
        error: action.error,
        msg: null,
        loading: false
    })
}

const verifySuccess = (state, action) => {
    return updateObject(state, {
        error: null,
        msg: action.msg,
        loading: false
    })
}

const registerFail = (state, action) => {
    return updateObject(state, {
        error: action.error,
        msg: null,
        loading: false
    })
}

const registerSuccess = (state, action) => {
    return updateObject(state, {
        error: null,
        msg: action.msg,
        loading: false
    })
}


const reducer = (state = initialState, action) => {
    switch (action.type) {
        case actionTypes.LOGIN_START:
            return loginStart(state, action);
        case actionTypes.LOGIN_SUCCESS:
            return loginSuccess(state, action);
        case actionTypes.LOGIN_FAIL:
            return loginFail(state, action);
        case actionTypes.LOGOUT:
            return logout(state, action);
        case actionTypes.EMAIL_SEND_SUCCESS :
            return emailSendSuccess(state, action);
        case actionTypes.EMAIL_SEND_FAIL :
            return emailSendFail(state, action)
        case actionTypes.CHANGE_PASSWORD_SUCCESS :
            return changePasswordSuccess(state, action);
        case actionTypes.CHANGE_PASSWORD_FAIL :
            return changePasswordFail(state, action)
        case actionTypes.VERIFY_SUCCESS :
            return verifySuccess(state, action)
        case actionTypes.VERIFY_FAIL :
            return verifyFail(state, action)
        case actionTypes.REGISTER_SUCCESS :
            return registerSuccess(state, action)
        case actionTypes.REGISTER_FAIL :
            return registerFail(state, action)

        default:
            return state;
    }
}

export default reducer;