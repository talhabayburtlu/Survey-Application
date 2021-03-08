import React, {useEffect} from "react";
import {connect} from 'react-redux'
import qs from 'qs'
import * as actions from "../store/actions/index";
import {Button, Card, CardContent, Grid, Typography} from "@material-ui/core";

const Verify = (props) => {
    useEffect(() => {
        if (props.isAuthenticated) {
            props.history.push("/")
        } else {
            const queryStrings = qs.parse(props.location.search, {ignoreQueryPrefix: true})
            props.onVerify(queryStrings.email, queryStrings.token)
        }
    }, [])

    return (
        <React.Fragment>
            <Grid justify="center" container style={{margin: "150px auto", backgroundColor: "#333", width: "325px"}}>
                <Grid item xs={12}>
                    <Card>
                        <CardContent style={{padding: "25px 50px"}}>
                            {props.msg ? <Typography gutterBottom variant="h5" style={{margin: "30px 0px"}}>
                                {props.msg}
                            </Typography> : null}
                            {props.err ? <Typography gutterBottom variant="h5" style={{margin: "30px 0px"}}>
                                {props.err}
                            </Typography> : null}
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={() => props.history.push("/login")}
                                type="submit"
                                fullWidth
                                style={{margin: "15px auto"}}
                            >Go to site</Button>
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
        onVerify: (email, token) => dispatch(actions.verifyAccount(email, token)),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(Verify);