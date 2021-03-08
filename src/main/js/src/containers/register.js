import React, {useEffect, useState} from "react";
import {connect} from 'react-redux'
import * as actions from "../store/actions/index";
import {Button, Card, CardContent, Grid, TextField, Typography} from "@material-ui/core";

const Register = (props) => {
    const [fullname, setFullname] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [age, setAge] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");


    useEffect(() => {
        if (props.isAuthenticated) {
            props.history.push("/")
        }
    }, [])


    return (
        <React.Fragment>
            <Grid justify="center" container style={{margin: "250px auto", backgroundColor: "#333", width: "325px"}}>
                <Grid item xs={12}>
                    <Card>
                        <CardContent style={{padding: "25px 50px"}}>
                            <Typography gutterBottom variant="h4" style={{margin: "30px 0px"}}>
                                Register
                            </Typography>
                            <form autoComplete="off">
                                <TextField
                                    id="standard-basic"
                                    label="Fullname" value={fullname}
                                    onChange={(e) => {
                                        setFullname(e.target.value)
                                    }}
                                    style={{margin: "15px auto"}}/>
                                <TextField
                                    id="standard-basic"
                                    label="Email" value={email}
                                    onChange={(e) => {
                                        setEmail(e.target.value)
                                    }}
                                    style={{margin: "15px auto"}}/>
                                <TextField
                                    id="standard-basic"
                                    label="Password"
                                    type="password"
                                    value={password}
                                    onChange={(e) => {
                                        setPassword(e.target.value)
                                    }}
                                    style={{margin: "15px auto"}}/>
                                <TextField
                                    id="standard-basic"
                                    label="Age" value={age}
                                    onChange={(e) => {
                                        setAge(e.target.value)
                                    }}
                                    style={{margin: "15px auto"}}/>
                                <TextField
                                    id="standard-basic"
                                    label="Phone Number" value={phoneNumber}
                                    onChange={(e) => {
                                        setPhoneNumber(e.target.value)
                                    }}
                                    style={{margin: "15px auto"}}/>
                            </form>
                            {props.err === "" ? null : <Typography style={{color: "#FF0000"}}>{props.err}</Typography>}
                            {props.msg === "" ? null : <Typography style={{color: "#008000"}}>{props.msg}</Typography>}
                            <Button
                                variant="contained"
                                color="primary"
                                fullWidth
                                onClick={() => props.onRegister(fullname, email, password, age, phoneNumber)}
                                type="submit"
                                style={{margin: "30px auto"}}
                            >Register</Button>
                            <Button
                                color="secondary"
                                variant="contained"
                                fullWidth
                                onClick={() => props.history.push("/login")}
                            >You have account? Login Here</Button>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </React.Fragment>
    )
}

const mapStateToProps = state => {
    return {
        isAuthenticated: state.auth.isAuthenticated,
        err: state.auth.error,
        msg: state.auth.msg
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onRegister: (fullname, email, password, age, phoneNumber) => dispatch(actions.registerAccount(fullname, email, password, age, phoneNumber)),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(Register);