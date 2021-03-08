import {Card, CardContent, Divider, Grid, Typography} from "@material-ui/core";
import Pagination from '@material-ui/lab/Pagination';
import React, {useEffect, useState} from "react";
import {connect} from 'react-redux'
import axios from "axios";


const Results = (props) => {
    const [pageCount, setPageCount] = useState(1);
    const [page, setPage] = useState(1)
    const [topics, setTopics] = useState([])

    const fetchResults = () => {
        axios.get("/topics/results?page=" + page, {headers: {'Authorization': `Bearer ${props.token}`}})
            .then((res) => {
                setPageCount(Math.ceil(res.data.totalDocumentCount / 15.0))
                setTopics(res.data.resultResources.slice())
            }).catch(e => {

        })
    }

    useEffect(() => {
        fetchResults()
    }, [page]);

    return (
        <React.Fragment>
            <Grid container direction="row" justify="center" alignItems="center">
                <Typography variant="h4" style={{margin: "15px"}}>Survey Results</Typography>
                {topics !== null && topics !== undefined ? topics.map(element => {
                        return (
                            <Grid item xs={12} key={element.question} style={{margin: "15px 100px"}}>
                                <Card>
                                    <CardContent>
                                        <Typography variant="h6" style={{
                                            fontWeight: "600",
                                            margin: "5px 0px"
                                        }}>{element.question}</Typography>
                                        <Divider style={{margin: "5px 0px"}}/>
                                        {element.responses.map(response => {
                                            return (
                                                <Typography key={response["option-no"]} variant="body1"
                                                            style={{margin: "5px 0px"}}>{response.option} :
                                                    Chosed {response["response-count"]} times.</Typography>
                                            )
                                        })}
                                    </CardContent>
                                </Card>
                            </Grid>)
                    }
                ) : null}
                <Pagination page={page} count={pageCount} onChange={(e, v) => setPage(v)} color="primary"
                            style={{textAlign: "center", margin: "25px auto"}}/>
            </Grid>
        </React.Fragment>

    )
}

const mapStateToProps = state => {
    return {
        token: state.auth.token,
        isAuthenticated: state.auth.isAuthenticated,
    };
};

export default connect(mapStateToProps)(Results);