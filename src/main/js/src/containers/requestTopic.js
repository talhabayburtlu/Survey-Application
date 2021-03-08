import {Button, Card, CardContent, Divider, FormControl, Grid, TextField, Typography} from "@material-ui/core";
import React, {useState} from "react";
import {connect} from 'react-redux'
import {withRouter} from 'react-router'
import axios from "axios";

const RequestTopic = (props) => {
    const [topicDescription, setTopicDescription] = useState("");
    const [answers, setAnswers] = useState([{description: ""}]);
    const [error, setError] = useState(null)

    const submitForm = () => {
        const body = {}
        body.answers = []

        body.description = topicDescription
        answers.map(answer => {
            body.answers.push({description: answer.description})
        })

        axios.post("/topics/", body, {headers: {'Authorization': `Bearer ${props.token}`}})
            .then((res) => {
                props.history.push("/")
            }).catch(e => {
            setError("Can't request, check your descriptions.")
        })


    }

    const addNewAnswer = () => {
        const newAnswers = answers.slice();
        newAnswers.push({description: ""})
        setAnswers(newAnswers)
    }

    return (
        <React.Fragment>
            <Grid justify="center" container style={{margin: "150px auto", backgroundColor: "#333", width: "700px"}}>
                <Grid item align="center" xs={12}>
                    <Card>
                        <CardContent style={{padding: "25px 50px"}}>
                            <Typography gutterBottom variant="h4" style={{margin: "30px 0px", textAlign: "center"}}>
                                Write A Topic and Answer(s)
                            </Typography>
                            <FormControl fullWidth autoComplete="off" style={{textAlign: "center"}}>
                                <TextField
                                    label="Topic Description" value={topicDescription}
                                    required
                                    onChange={(e) => {
                                        setTopicDescription(e.target.value)
                                    }}
                                    fullWidth
                                    style={{margin: "15px auto",}}/>
                                <Divider/>

                                {answers.map((answer, index) => {
                                    return (
                                        <Grid container alignItems="center" key={index}>
                                            <TextField
                                                key={index}
                                                required
                                                label="Answer Description" value={answer.description}
                                                onChange={(e) => {
                                                    const newAnswers = answers.slice()
                                                    newAnswers[index].description = e.target.value
                                                    setAnswers(newAnswers)
                                                }}
                                                style={{margin: "15px 0px 15px 0px", width: "85%"}}
                                            />
                                            {index !== 0 ? <Button
                                                variant="contained"
                                                color="secondary"
                                                onClick={(e) => {
                                                    let newAnswers = answers.slice(0, index)
                                                    newAnswers = newAnswers.concat(answers.slice(index + 1))
                                                    setAnswers(newAnswers)
                                                }}
                                                style={{margin: "5px 0px", height: "35px"}}
                                            >Delete</Button> : null}
                                        </Grid>

                                    )
                                })}
                                {error ? <Typography style={{color: "#FF0000"}}>{error}</Typography> : null}
                            </FormControl>
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={submitForm}
                                type="submit"
                                style={{margin: "15px 15px"}}
                            >{props.role === "ADMIN" ? "Create Topic" : "Request Topic"} </Button>
                            <Button
                                variant="contained"
                                color="secondary"
                                onClick={addNewAnswer}
                                style={{margin: "15px 15px"}}
                            >Add new answer</Button>
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
        role: state.auth.role
    };
};

export default withRouter(connect(mapStateToProps)(RequestTopic));