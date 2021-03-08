import {Button, Card, CardContent, Grid, TextField, Typography} from "@material-ui/core";
import React, {useState} from "react";
import {connect} from 'react-redux'
import qs from 'qs'
import * as actions from "../store/actions/index";

const ChangePassword = (props) => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const submitForm = () => {
        if (props.isAuthenticated) {
            props.onAuthenticatedChangePassword(props.token, password)
        } else {
            props.onUnauthenticatedChangePassword(qs.parse(props.location.search, {ignoreQueryPrefix: true}).token,
                email, password)
        }
    }

    return (
        <React.Fragment>
            <Grid justify="center" container style={{margin: "100px auto", backgroundColor: "#333", width: "325px"}}>
                <Grid item xs={12}>
                    <Card>
                        <CardContent style={{padding: "25px 50px"}}>
                            <Typography gutterBottom variant="h4" style={{margin: "30px 0px"}}>
                                Change Password
                            </Typography>
                            <form autoComplete="off">
                                {!props.isAuthenticated ? <TextField
                                    id="standard-basic"
                                    label="Email" value={email}
                                    onChange={(e) => {
                                        setEmail(e.target.value)
                                    }}
                                    style={{margin: "15px auto"}}/> : null}
                                <TextField
                                    id="standard-basic"
                                    label="Password"
                                    type="password"
                                    value={password}
                                    onChange={(e) => {
                                        setPassword(e.target.value)
                                    }}
                                    style={{margin: "15px auto"}}/>
                            </form>
                            {props.err === "" ? null : <Typography style={{color: "#FF0000"}}>{props.err}</Typography>}
                            {props.msg === "" ? null : <Typography style={{color: "#008000"}}>{props.msg}</Typography>}
                            <Button
                                variant="contained"
                                color="primary"
                                fullWidth
                                onClick={submitForm}
                                type="submit"
                                style={{margin: "15px auto"}}
                            >Change password</Button>
                            <Button
                                variant="contained"
                                color="secondary"
                                fullWidth
                                onClick={() => {
                                    props.history.push("/login")
                                }}
                                type="submit"
                                style={{margin: "15px auto"}}
                            >Log In</Button>
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
        onUnauthenticatedChangePassword: (token, email, password) => dispatch(actions.unauthenticatedChangePassword(token, email, password)),
        onAuthenticatedChangePassword: (authToken, password) => dispatch(actions.authenticatedChangePassword(authToken, password))
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(ChangePassword);