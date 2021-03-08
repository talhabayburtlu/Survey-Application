import {Button, Card, CardContent, Grid, TextField, Typography} from "@material-ui/core";
import React, {useEffect, useState} from "react";
import {connect} from 'react-redux'
import * as actions from "../store/actions/index";


const Login = (props) => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [forgotPasswordOn, setForgotPasswordOn] = useState(false);

    const submitForm = () => {
        if (!forgotPasswordOn)
            props.onLogin(email, password, props);
        else
            props.onSendForgetPasswordMail(email)
    }

    useEffect(() => {
        if (props.isAuthenticated) {
            props.history.push("/")
        }
    }, []);

    return (
        <React.Fragment>
            <Grid justify="center" container style={{margin: "150px auto", backgroundColor: "#333", width: "325px"}}>
                <Grid item xs={12}>
                    <Card>
                        <CardContent style={{padding: "25px 50px"}}>
                            <Typography gutterBottom variant="h4" style={{margin: "30px 0px", textAlign: "center"}}>
                                {forgotPasswordOn ? "Forgot Password" : "Log In"}
                            </Typography>
                            <form autoComplete="off" style={{textAlign: "center"}}>
                                <TextField
                                    id="standard-basic"
                                    label="Email" value={email}
                                    onChange={(e) => {
                                        setEmail(e.target.value)
                                    }}
                                    style={{margin: "15px auto",}}/>
                                {!forgotPasswordOn ? <TextField
                                    id="standard-basic"
                                    label="Password"
                                    type="password"
                                    value={password}
                                    onChange={(e) => {
                                        setPassword(e.target.value)
                                    }}
                                    style={{margin: "15px auto"}}/> : null}
                            </form>
                            <Button
                                variant="contained"
                                color="primary"
                                fullWidth
                                onClick={submitForm}
                                type="submit"
                                style={{margin: "15px auto"}}
                            >{!forgotPasswordOn ? "Log in" : "Send Email"}</Button>
                            {props.err === "" ? null : <Typography style={{color: "#FF0000"}}>{props.err}</Typography>}
                            {props.msg === "" ? null : <Typography style={{color: "#008000"}}>{props.msg}</Typography>}
                            {!forgotPasswordOn ? <Button
                                    color="secondary"
                                    variant="contained"
                                    fullWidth
                                    onClick={() => setForgotPasswordOn(true)}
                                    style={{margin: "15px 0px"}}
                                >Forgot my password</Button> :
                                <Button
                                    color="secondary"
                                    variant="contained"
                                    fullWidth
                                    onClick={() => setForgotPasswordOn(false)}
                                    style={{margin: "px 0px"}}
                                >Log in</Button>
                            }
                            <Button
                                color="secondary"
                                variant="contained"
                                fullWidth
                                onClick={() => props.history.push("/register")}
                                style={{margin: "15px 0px"}}
                            >Don't Have Account? Register Here</Button>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </React.Fragment>
    )
}

const mapStateToProps = state => {
    return {
        token: state.auth.token,
        isAuthenticated: state.auth.isAuthenticated,
        err: state.auth.error,
        msg: state.auth.msg
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onLogin: (email, password, props) => dispatch(actions.login(email, password, props)),
        onLogout: () => dispatch(actions.logout()),
        onSendForgetPasswordMail: (email) => dispatch(actions.sendForgetPasswordEmail(email))
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(Login);